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
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter{

	public String reason;
	public Long pauseMessage;

	@Override
	public void onPlayerPause(AudioPlayer player) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

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
						channel.sendMessage(builder.build()).queue();
					}
				}
			}
		} catch (SQLException e) { }
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		System.out.println("Resume ID : " + pauseMessage);
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription("Musik läuft weiter.");

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
						channel.deleteMessageById(pauseMessage);

						channel.sendMessage(builder.build()).complete().delete().  queueAfter(5, TimeUnit.SECONDS);
					}
				}
			}
		} catch (SQLException e) { }
	}


	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());

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

		MusicUtil.sendEmbed(guildid, builder);
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		long guildid = DragonBot.INSTANCE.playerManager.getGuildByPlayerHash(player.hashCode());
		Guild guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid);

		if(endReason.mayStartNext) {
			MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
			Queue queue = controller.getQueue();

			if(queue.isNext()) {
				queue.playNext();
				return;
			}
		} else if (endReason.toString() == "REPLACED"){ 
			return;
		}

		AudioManager manager = guild.getAudioManager();
		player.stopTrack();
		manager.closeAudioConnection();
	}

}
