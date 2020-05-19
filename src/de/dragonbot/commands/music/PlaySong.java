package de.dragonbot.commands.music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.AudioLoadResult;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlaySong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		
		TextChannel dashboard = Utils.getDashboardChannel(channel.getGuild());

		if(dashboard == null) {
			message.delete().queue();
		} else if (dashboard != channel) {
			message.delete().queue();
		}

		
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

					Utils.setMusicChannel(channel);

					StringBuilder strBuilder = new StringBuilder();
					for(int i = 1; i < args.length; i++) strBuilder.append(args[i] + " ");


					String url = strBuilder.toString().trim();
					if(!url.startsWith("http")) {
						url = "ytsearch: " + url;
					}

					controller.SetLoadedFromInternalPlaylist(false);
					apm.loadItem(url, new AudioLoadResult(controller));
				}
				else {
					Utils.sendEmbed("ERROR", "Bitte joine einem VoiceChannel, um diesen Command zu benutzen.", channel, 3l, new Color(0xff0000));
				}
			}
			else {
				Utils.sendEmbed("ERROR", "Bitte joine einem VoiceChannel, um diesen Command zu benutzen.", channel, 3l, new Color(0xff0000));
			}
		}
		else {
			Utils.sendEmbed("ERROR", "Falsche Syntax! \n" + "Bitte nutze `#d play <url/suchbegriff>`", channel, 3l, new Color(0xff0000));
		}

	}
}
