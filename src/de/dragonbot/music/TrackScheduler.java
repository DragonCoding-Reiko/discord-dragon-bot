package de.dragonbot.music;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.dragonbot.DragonBot;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter{

	public String reason;
	public Message pauseMessage;

	@Override
	public void onPlayerPause(AudioPlayer player) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

		String sql_SELECT_MusicSettings = "SELECT `now_playing` "
										+ "FROM `Music_Settings` "
										+ "WHERE guild_ID = " + guildid;
		
		ResultSet isNP = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_MusicSettings);

		try {
			if(isNP.next()) {
				if(!isNP.getBoolean("now_playing")) {

					EmbedBuilder builder = new EmbedBuilder();
					builder.setDescription("Musik pausiert.");

					String sql_SELECT_MusicChannel = "SELECT `channel_ID` "
												   + "FROM `Music_Channel` "
												   + "WHERE guild_ID = " + guildid;
					
					ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_MusicChannel);

					if(set.next()) {
						long channelid = set.getLong("channel_ID");
						Guild guild;
						if((guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid)) != null) {
							TextChannel channel;
							if((channel = guild.getTextChannelById(channelid)) != null) {
								pauseMessage = channel.sendMessage(builder.build()).complete();
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

		String sql_SELECT_MusicSettings = "SELECT `now_playing` "
				+ "FROM `Music_Settings` "
				+ "WHERE guild_ID = " + guildid;

		ResultSet isNP = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_MusicSettings);

		try {
			if(isNP.next()) {
				if(!isNP.getBoolean("now_playing")) {

					EmbedBuilder builder = new EmbedBuilder();
					builder.setDescription("Musik läuft weiter.");

					String sql_SELECT_MusicChannel = "SELECT `channel_ID` "
							   + "FROM `Music_Channel` "
							   + "WHERE guild_ID = " + guildid;

					ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_MusicChannel);

					if(set.next()) {
						long channelid = set.getLong("channel_ID");
						Guild guild;
						if((guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid)) != null) {
							TextChannel channel;
							if((channel = guild.getTextChannelById(channelid)) != null) {
								pauseMessage.delete().queue();

								channel.sendMessage(builder.build()).complete().delete().  queueAfter(5, TimeUnit.SECONDS);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
	}


	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

		String sql_SELECT_MusicSettings = "SELECT `now_playing` "
				+ "FROM `Music_Settings` "
				+ "WHERE guild_ID = " + guildid;

		ResultSet isNP = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_MusicSettings);

		try {
			if(isNP.next()) {
				if(!isNP.getBoolean("now_playing")) {
					EmbedBuilder builder = new EmbedBuilder();
					builder.setColor(Color.decode("#00e640"));
					AudioTrackInfo info = track.getInfo();
					builder.setDescription("Now Playing: \n" + info.title);

					long sekunden = info.length/1000;
					long minuten = sekunden/60;
					long stunden = minuten/60;
					sekunden %= 60;
					minuten %= 60;
					stunden %= 60;

					String url = info.uri;

					builder.addField(info.author, "[" + info.title + "](" + url + ")", false);
					builder.addField("Länge", info.isStream ? "Live(Stream)" : (stunden > 0 ? stunden + "h " : "") + minuten + "min " + sekunden + "s", true);

					Utils.sendEmbed(builder, Utils.getMusicChannel(guildid), 5l, null);
				} else {
					MusicDashboard.onStartPlaying(DragonBot.INSTANCE.shardMan.getGuildById(guildid));
					
				}
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}

	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());
		Guild guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid);
		MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
		Queue queue = controller.getQueue();


		if(endReason.mayStartNext) {

			if(queue.isNext()) {
				queue.playNext();
				return;
			}
		}

		if(endReason.toString().equalsIgnoreCase("REPLACED")) {
			return;
		}

		AudioManager manager = guild.getAudioManager();
		player.stopTrack();
		queue.setFirst(true);

		TextChannel dashboard = Utils.getDashboardChannel(guild);
		if(dashboard != null) {
			MusicDashboard.onAFK(dashboard);
		}

		manager.closeAudioConnection();
	}

}
