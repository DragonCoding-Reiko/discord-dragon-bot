package de.dragonbot.commands.music;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class LoopSong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();
		//loop single

		GuildVoiceState state;
		if((state = m.getVoiceState()) != null) {
			VoiceChannel vc;
			if((vc = state.getChannel()) != null) {
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());

				//#d loop single
				String[] args = message.getContentDisplay().substring(subString).split(" ");

				//loop single
				if(args.length == 1) {
					System.out.println("Loop");
					controller.getQueue().loop(channel);

				} else if(args[1].toLowerCase().contains("single")) {
					System.out.println("Single Loop");
					controller.getQueue().singleLoop();

				}

			}
		}

	}

}
