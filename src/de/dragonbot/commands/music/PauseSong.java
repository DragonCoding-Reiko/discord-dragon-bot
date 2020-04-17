package de.dragonbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PauseSong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		Guild guild = channel.getGuild();
		Long guildid = guild.getIdLong();
		MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
		AudioPlayer player = controller.getPlayer();

		if(player.isPaused()) {
			player.setPaused(false);
		} else {
			player.setPaused(true);
		}
	}

}
