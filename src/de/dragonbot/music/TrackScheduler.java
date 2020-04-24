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
import de.dragonbot.manage.LiteSQL;
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

		ResultSet isNP = LiteSQL.getEntrys("nowplaying", "musicsettings", 
				"guildid = " + guildid);

		try {
			if(isNP.next()) {
				if(!isNP.getBoolean("nowplaying")) {

					EmbedBuilder builder = new EmbedBuilder();
					builder.setDescription("Musik pausiert.");

					ResultSet set = LiteSQL.getEntrys("*", 
							"musicchannel", 
							"guildid = " + guildid);

					try {
						if(set.next()) {
							long channelid = set.getLong("channelid");
							Guild guild;
							if((guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid)) != null) {
								TextChannel channel;
								if((channel = guild.getTextChannelById(channelid)) != null) {
									pauseMessage = channel.sendMessage(builder.build()).complete();
								}
							}
						}
					} catch (SQLException e) { }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

		ResultSet isNP = LiteSQL.getEntrys("nowplaying", "musicsettings", 
				"guildid = " + guildid);

		try {
			if(isNP.next()) {
				if(!isNP.getBoolean("nowplaying")) {

					EmbedBuilder builder = new EmbedBuilder();
					builder.setDescription("Musik l�uft weiter.");

					ResultSet set = LiteSQL.getEntrys("*", 
							"musicchannel", 
							"guildid = " + guildid);

					try {
						if(set.next()) {
							long channelid = set.getLong("channelid");
							Guild guild;
							if((guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid)) != null) {
								TextChannel channel;
								if((channel = guild.getTextChannelById(channelid)) != null) {
									pauseMessage.delete().queue();

									channel.sendMessage(builder.build()).complete().delete().  queueAfter(5, TimeUnit.SECONDS);
								}
							}
						}
					} catch (SQLException e) { }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

		ResultSet isNP = LiteSQL.getEntrys("nowplaying", "musicsettings", 
				"guildid = " + guildid);

		try {
			if(isNP.next()) {
				if(!isNP.getBoolean("nowplaying")) {
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
					builder.addField("L�nge", info.isStream ? "Live(Stream)" : (stunden > 0 ? stunden + "h " : "") + minuten + "min " + sekunden + "s", true);

					MusicUtil.sendEmbed(guildid, builder);
				} else {
					MusicDashboard.onStartPlaying(DragonBot.INSTANCE.shardMan.getGuildById(guildid));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
		ResultSet set = LiteSQL.getEntrys("channelid",
				"npchannel",
				"guildid = " + guild.getIdLong());

		try {
			if(set.next()) {
				long channelID = set.getLong("channelid");
				TextChannel channel = guild.getTextChannelById(channelID);

				MusicDashboard.onAFK(channel);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		manager.closeAudioConnection();
	}

}