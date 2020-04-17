package de.dragonbot.commands.music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.AudioLoadResult;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.MusicUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlaySong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		if(args.length > 1) {
			GuildVoiceState state;
			if((state = m.getVoiceState()) != null) {
				VoiceChannel vc;
				if((vc = state.getChannel()) != null) {
					MusicController controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
					AudioPlayerManager apm = DragonBot.INSTANCE.audioPlayermanager;
					AudioManager manager = vc.getGuild().getAudioManager();

					manager.openAudioConnection(vc);

					MusicUtil.updateChannel(channel);

					StringBuilder strBuilder = new StringBuilder();
					for(int i = 1; i < args.length; i++) strBuilder.append(args[i] + " ");


					String url = strBuilder.toString().trim();
					if(!url.startsWith("http")) {
						url = "ytsearch: " + url;
					}

					apm.loadItem(url, new AudioLoadResult(controller));
				}
				else {
					EmbedBuilder builder = new EmbedBuilder();
					builder.setDescription("Bitte joine einem VoiceChannel, um diesen Command zu benutzen.");
					builder.setColor(Color.decode("#2980b9"));
					channel.sendMessage(builder.build()).queue();
				}
			}
			else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setDescription("Bitte joine einem VoiceChannel, um diesen Command zu benutzen.");
				builder.setColor(Color.decode("#2980b9"));
				channel.sendMessage(builder.build()).queue();
			}
		}
		else {
			EmbedBuilder builder = new EmbedBuilder();
			builder.setDescription("Bitte nutze `#d play <url/suchbegriff>`");
			builder.setColor(Color.decode("#2980b9"));
			channel.sendMessage(builder.build()).queue();
		}

	}
}
