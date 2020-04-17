package de.dragonbot.commands.music;

import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class NowPlaying implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		MusicController controller = DragonBot.INSTANCE.playerManager.getController(channel.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		AudioTrack track;

		if((track = player.getPlayingTrack()) != null) {
			AudioTrackInfo info = track.getInfo();

			String author = info.author;
			String title = info.title;
			String url = info.uri;
			boolean isStream = info.isStream;
			long position = track.getPosition();
			long length = track.getDuration();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(author);
			builder.setTitle(title, url);

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

			builder.setDescription(isStream ? "Live (Stream)" : time);

			channel.sendMessage(builder.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
		}
		else {
			channel.sendMessage("Es wird kein Song gespielt.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
		}
	}

}
