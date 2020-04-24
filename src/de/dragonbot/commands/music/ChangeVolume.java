package de.dragonbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ChangeVolume implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		//Min Max Up Down
		//10  100 +5  -5

		GuildVoiceState state;
		VoiceChannel vc;
		MusicController controller;
		AudioPlayer player = null;

		if((state = m.getVoiceState()) != null) {
			if((vc = state.getChannel()) != null) {
				controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
				player =  controller.getPlayer();
			}
		}

		if(player != null) {
			
			switch (args[1].toLowerCase()) {
			case "min":
				setVolume(5, player);
				break;
			case "max":
				setVolume(100, player);
				break;
			case "up":
				changeVolume(+5, player);
				break;
			case "down":
				changeVolume(-5, player);
				break;
			case "set":
				if(args.length == 2) {
					int setter = 0;
					try { Integer.parseInt(args[2]); } catch (NumberFormatException e) { }
					if(setter >= 5 && setter <= 100) {
						setVolume(setter, player);
					}			
				}
				break;
				
			default:
				break;
			}
		}
	}

	public static void changeVolume(int value, AudioPlayer player) {
		int test = player.getVolume() + value;
		if(test >= 5 && test <= 100) {
			player.setVolume(value);
		}
	}
	
	public static void setVolume(int value, AudioPlayer player) {
		player.setVolume(value);
	}
	
}
