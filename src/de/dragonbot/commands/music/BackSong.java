package de.dragonbot.commands.music;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class BackSong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		Guild guild = channel.getGuild();
		Long guildid = guild.getIdLong();
		MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
		Queue queue = controller.getQueue();

		queue.playLast();
	}

}
