package de.dragonbot.commands.music;

import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ShuffleOueue implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		GuildVoiceState state;
		if((state = m.getVoiceState()) != null) {
			VoiceChannel vc;
			if((vc = state.getChannel()) != null) {
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());

				controller.getQueue().shuffle();
				channel.sendMessage(":twisted_rightwards_arrows: Playlist geshuffled.").complete().delete().queueAfter(10, TimeUnit.SECONDS);
			}
		}
	}
}
