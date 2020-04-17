package de.dragonbot.commands.random;

import java.util.concurrent.TimeUnit;

import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.DONOTOPEN;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Invite implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int substring) {
		message.delete().queue();

		channel.sendMessage("Lad mich zu deinem Discord ein^^ \n" + "**Link: **" + DONOTOPEN.invite).complete().delete().queueAfter(20, TimeUnit.SECONDS);

	}

}
