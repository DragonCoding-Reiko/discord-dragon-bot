package de.dragonbot.music;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.dragonbot.DragonBot;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicDashboard {

	public static void onStartUp() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			TextChannel channel = Utils.getDashboardChannel(guild);

			if(channel != null) {
				onAFK(channel);
			}
		}); 
	}

	public static void onShutdown() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			TextChannel channel = Utils.getDashboardChannel(guild);

			if(channel != null) {
				List<Message> messages = new ArrayList<>();

				for(Message message : channel.getIterableHistory().cache(false)) { 
					messages.add(message);
				}

				channel.purgeMessages(messages);
				
				Utils.sendEmbed("🔴 BOT offline", "Sorry, we're trying to change this state. \n" + "...but the squirrels keep munching on our cables.", channel, 0l, new Color(0xff0000));
			}
		}); 
	}
	
	public static void onAFK(TextChannel channel) {
		List<Message> messages = new ArrayList<>();

		for(Message message : channel.getIterableHistory().cache(false)) { 
			messages.add(message);
		}

		channel.purgeMessages(messages);

		String sql_REMOVE_Messages = "DELETE FROM `Messages` "
								   + "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + channel.getIdLong();
		
		DragonBot.INSTANCE.loopDB.execute(sql_REMOVE_Messages);

		Utils.sendEmbed("✅ BOT ONLINE", "No music playing. \n" + "Use #d play to start a song.", channel, 0l, new Color(0xff0000));

	}

	public static void onStartPlaying(Guild guild) {

			Long guildid = guild.getIdLong();
			TextChannel channel = Utils.getDashboardChannel(guild);
			
			if(channel != null) {
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
				Queue queue = controller.getQueue();
	
				if(queue.isFirst()) {
					queue.setFirst(false);
						if(channel != null) {
							AudioPlayer player = controller.getPlayer();
							List<Message> messages = new ArrayList<>();
	
							for(Message message : channel.getIterableHistory().cache(false)) { 
								messages.add(message);
							}
	
							channel.purgeMessages(messages);
	
							Message msg1 = sendQueue(channel, controller, player, queue);
							Message msg2 = sendNowPlaying(channel, player, queue);
							Message msg3 = sendVolume(channel, player);
	
							String sql_INSERT_NewMessages = "INSERT INTO `Messages`(`guild_ID`, `channel_ID`, `message_ID_1`, `message_ID_2`, `message_ID_3`) "
														  + "VALUES (" + guildid + ", " + channel.getIdLong() + ", " + msg1.getIdLong() + ", " + msg2.getIdLong() + ", " + msg3.getIdLong() + ")";
							
							DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewMessages);
						}
				}
			}
	}

	public static void update() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {

			Long guildid = guild.getIdLong();
			MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
			AudioPlayer player = controller.getPlayer();
			
			if(player.getPlayingTrack() != null) {
				TextChannel channel = Utils.getDashboardChannel(guild);
				
				if(channel != null) {
					Queue queue = controller.getQueue();

					updateNowPlaying(channel, player, queue);
				}

			}
		}); 
	}

	//Just to make things more readable
	public static Message sendQueue(TextChannel channel, MusicController controller, AudioPlayer player, Queue queue) {
		List<AudioTrack> list = queue.getQueueList();
		int length = list.size();

		EmbedBuilder queueMsg = new EmbedBuilder();

		queueMsg.setTitle("**The next 5 songs:**");
		queueMsg.setFooter("Next 5 of " + length + " \n" + "---------------------------------------------------------------------------------------------------------------");

		int counter = 1;
		for(AudioTrack track : list) {
			String author = track.getInfo().author;
			String title = track.getInfo().title;
			String url = track.getInfo().uri;

			queueMsg.addField(author, "[" + title + "](" + url + ")", false);

			if(counter > 5) {
				break;
			}
			counter++;
		}

		Message message = channel.sendMessage(queueMsg.build()).complete();
		
		message.addReaction("❌").queue();
		message.addReaction("❓").queue();

		return message;
	}

	public static Message sendNowPlaying(TextChannel channel, AudioPlayer player, Queue queue) {
		AudioTrack np = player.getPlayingTrack();

		String author = np.getInfo().author;
		String title = np.getInfo().title;
		String url = np.getInfo().uri;
		boolean isStream = np.getInfo().isStream;
		boolean isPaused = player.isPaused();
		boolean isSingleLoop = queue.isSingleLoop();
		boolean isLoop = queue.isLoop();
		long position = np.getPosition();
		long length = np.getDuration();

		EmbedBuilder playingMsg = new EmbedBuilder();
		playingMsg.setAuthor(author);
		playingMsg.setTitle(title, url);

		long curSeconds = position / 1000;
		long curMinutes = curSeconds / 60;
		long curStunden = curMinutes / 60;
		curSeconds %= 60;
		curMinutes %= 60;

		long maxSeconds = length / 1000;
		long maxMinutes = maxSeconds / 60;
		long maxStunden = maxMinutes / 60;
		maxSeconds %= 60;
		maxMinutes %= 60;

		String time = ((curStunden > 0) ? curStunden + "h " :  "") + curMinutes + "min " + curSeconds + "s /" + ((maxStunden > 0) ? maxStunden + "h " :  "") + maxMinutes + "min " + maxSeconds + "s";

		playingMsg.setDescription((isStream ? "Live (Stream)" : time) + " \n "
				+ "State: " + (isPaused ? "paused" : "playing") + " \n "
				+ "Loop: " + (isSingleLoop ? "Single Song" : (isLoop ? "All" : "No Loop")));
		playingMsg.setFooter("---------------------------------------------------------------------------------------------------------------");

		Message message = channel.sendMessage(playingMsg.build()).complete();

		message.addReaction("🔀").queue();
		message.addReaction("⏹️").queue();
		message.addReaction("⏮️").queue();
		message.addReaction("⏸️").queue();
		message.addReaction("⏭️").queue();
		message.addReaction("🔁").queue();
		message.addReaction("🔂").queue();

		return message;
	}

	public static Message sendVolume(TextChannel channel, AudioPlayer player) {
		int volume = player.getVolume();

		EmbedBuilder volumeMsg = new EmbedBuilder();

		volumeMsg.setTitle("**Volume:**");
		volumeMsg.setDescription("" + volume);
		volumeMsg.setFooter("---------------------------------------------------------------------------------------------------------------");
		Message message = channel.sendMessage(volumeMsg.build()).complete();

		message.addReaction("🔉").queue();
		message.addReaction("🔊").queue();
		message.addReaction("🔄").queue();

		return message;
	}

	public static void updateQueue(TextChannel channel, MusicController controller, AudioPlayer player) {
		Queue queue = controller.getQueue();
		List<AudioTrack> list = queue.getQueueList();
		int length = list.size();

		EmbedBuilder queueMsg = new EmbedBuilder();

		queueMsg.setTitle("**The next 5 songs:**");
		queueMsg.setFooter("Next 5 of " + length + "\n" + "---------------------------------------------------------------------------------------------------------------");

		int counter = 1;
		for(AudioTrack track : list) {
			String author = track.getInfo().author;
			String title = track.getInfo().title;
			String url = track.getInfo().uri;

			queueMsg.addField(author, "[" + title + "](" + url + ")", false);

			if(counter > 5) {
				break;
			}
			counter++;
		}

		String sql_SELECT_Message1 = "SELECT `message_ID_1` "
								   + "FROM `Messages` "
								   + "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + channel.getIdLong();
		
		ResultSet set = DragonBot.INSTANCE.loopDB.getData(sql_SELECT_Message1);

		try {
			if(set.next()) {
				channel.editMessageById(set.getLong("message_ID_1"), queueMsg.build()).complete();
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
	}

	public static void updateNowPlaying(TextChannel channel, AudioPlayer player, Queue queue) {
		AudioTrack np = player.getPlayingTrack();

		String author = np.getInfo().author;
		String title = np.getInfo().title;
		String url = np.getInfo().uri;
		boolean isStream = np.getInfo().isStream;
		boolean isPaused = player.isPaused();
		boolean isSingleLoop = queue.isSingleLoop();
		boolean isLoop = queue.isLoop();
		long position = np.getPosition();
		long length = np.getDuration();

		EmbedBuilder playingMsg = new EmbedBuilder();
		playingMsg.setAuthor(author);
		playingMsg.setTitle(title, url);

		long curSeconds = position / 1000;
		long curMinutes = curSeconds / 60;
		long curStunden = curMinutes / 60;
		curSeconds %= 60;
		curMinutes %= 60;

		long maxSeconds = length / 1000;
		long maxMinutes = maxSeconds / 60;
		long maxStunden = maxMinutes / 60;
		maxSeconds %= 60;
		maxMinutes %= 60;

		String time = ((curStunden > 0) ? curStunden + "h " :  "") + curMinutes + "min " + curSeconds + "s / " + ((maxStunden > 0) ? maxStunden + "h " :  "") + maxMinutes + "min " + maxSeconds + "s";

		playingMsg.setDescription(
				"**__Time:__** " + (isStream ? "Live (Stream)" : time) + " \n \n"
						+ "**__State:__** " + (isPaused ? "Paused" : "Playing") + " \n "
						+ "**__Loop:__** " + (isSingleLoop ? "Single Song" : (isLoop ? "All" : "No Loop")));
		playingMsg.setFooter("---------------------------------------------------------------------------------------------------------------");

		String sql_SELECT_Message2 = "SELECT `message_ID_2` "
				   + "FROM `Messages` "
				   + "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + channel.getIdLong();

		ResultSet set = DragonBot.INSTANCE.loopDB.getData(sql_SELECT_Message2);

		try {
			if(set.next()) {
				channel.editMessageById(set.getLong("message_ID_2"), playingMsg.build()).queue();
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
	}

	public static void updateVolume(TextChannel channel, AudioPlayer player) {
		int volume = player.getVolume();

		EmbedBuilder volumeMsg = new EmbedBuilder();

		volumeMsg.setTitle("**Volume:**");
		volumeMsg.setDescription("" + volume);
		volumeMsg.setFooter("---------------------------------------------------------------------------------------------------------------");

		String sql_SELECT_Message3 = "SELECT `message_ID_3` "
				   + "FROM `Messages` "
				   + "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + channel.getIdLong();

		ResultSet set = DragonBot.INSTANCE.loopDB.getData(sql_SELECT_Message3);

		try {
			if(set.next()) {
				channel.editMessageById(set.getLong("message_ID_3"), volumeMsg.build()).queue();
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
	}
}






