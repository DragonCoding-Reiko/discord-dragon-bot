package de.dragonbot.music;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.random.SendAsEmbed;
import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicDashboard {

	public static void onStartUp() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.getEntrys("channelid",
					"npchannel",
					"guildid = " + guild.getIdLong());

			try {
				if(set.next()) {
					long channelID = set.getLong("channelid");
					TextChannel channel = guild.getTextChannelById(channelID);

					onAFK(channel);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}

	public static void onShutdown() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.getEntrys("channelid",
					"npchannel",
					"guildid = " + guild.getIdLong());
			try {
				if(set.next()) {
					long channelID = set.getLong("channelid");
					TextChannel channel = guild.getTextChannelById(channelID);

					List<Message> messages = new ArrayList<>();

					for(Message message : channel.getIterableHistory().cache(false)) { 
						messages.add(message);
					}

					channel.purgeMessages(messages);

					SendAsEmbed.sendEmbed(channel, "🔴 BOT offline - Sorry, we're trying to change this state. \n"
							+ "...but the squirrels keep munching on our cables.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}); 
	}

	public static void onAFK(TextChannel channel) {
		List<Message> messages = new ArrayList<>();

		for(Message message : channel.getIterableHistory().cache(false)) { 
			messages.add(message);
		}

		channel.purgeMessages(messages);

		LiteSQL.deleteEntry("messages", 
				"guildid = " + channel.getGuild().getIdLong() + " AND channelid = " + channel.getIdLong());

		SendAsEmbed.sendEmbed(channel, "✅ BOT ONLINE - No music playing. \n" 
				+ "Use #d play to start a song.");
	}

	public static void onStartPlaying(Guild guild) {

			ResultSet set = LiteSQL.getEntrys("channelid",
					"npchannel",
					"guildid = " + guild.getIdLong());

			Long guildid = guild.getIdLong();
			MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
			Queue queue = controller.getQueue();

			if(queue.isFirst()) {
				queue.setFirst(false);
				try {
					if(set.next()) {
						TextChannel channel = guild.getTextChannelById(set.getLong("channelid"));
						AudioPlayer player = controller.getPlayer();
						List<Message> messages = new ArrayList<>();

						for(Message message : channel.getIterableHistory().cache(false)) { 
							messages.add(message);
						}

						channel.purgeMessages(messages);

						Message msg1 = sendQueue(channel, controller, player, queue);
						Message msg2 = sendNowPlaying(channel, player, queue);
						Message msg3 = sendVolume(channel, player);

						LiteSQL.newEntry("messages", "guildid, channelid, msg1, msg2, msg3",
								guildid + ", " + set.getLong("channelid") + ", " + msg1.getIdLong() + ", " + msg2.getIdLong() + ", " + msg3.getIdLong());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}

	public static void update() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {

			Long guildid = guild.getIdLong();
			MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
			AudioPlayer player = controller.getPlayer();
			if(player.getPlayingTrack() != null) {
				ResultSet set = LiteSQL.getEntrys("channelid",
						"npchannel",
						"guildid = " + guild.getIdLong());

				try {
					if(set.next()) {

						TextChannel channel = guild.getTextChannelById(set.getLong("channelid"));

						Queue queue = controller.getQueue();

						updateNowPlaying(channel, player, queue);
						updateQueue(channel, controller, player);
					}
				} catch (SQLException e) {
					e.printStackTrace();
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

		ResultSet set = LiteSQL.getEntrys("msg1", "messages", 
				"guildid = " + channel.getGuild().getIdLong() + " AND channelid = " + channel.getIdLong());

		try {
			if(set.next()) {
				channel.editMessageById(set.getLong("msg1"), queueMsg.build()).complete();
			}
		} catch (SQLException e) {
			e.printStackTrace();
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

		ResultSet set = LiteSQL.getEntrys("msg2", "messages", 
				"guildid = " + channel.getGuild().getIdLong() + " AND channelid = " + channel.getIdLong());

		try {
			if(set.next()) {
				channel.editMessageById(set.getLong("msg2"), playingMsg.build()).queue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateVolume(TextChannel channel, AudioPlayer player) {
		int volume = player.getVolume();

		EmbedBuilder volumeMsg = new EmbedBuilder();

		volumeMsg.setTitle("**Volume:**");
		volumeMsg.setDescription("" + volume);
		volumeMsg.setFooter("---------------------------------------------------------------------------------------------------------------");

		ResultSet set = LiteSQL.getEntrys("msg3", "messages", 
				"guildid = " + channel.getGuild().getIdLong() + " AND channelid = " + channel.getIdLong());

		try {
			if(set.next()) {
				channel.editMessageById(set.getLong("msg3"), volumeMsg.build()).queue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}






