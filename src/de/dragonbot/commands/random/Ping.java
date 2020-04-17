package de.dragonbot.commands.random;

import de.dragonbot.commands.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Ping implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		long gatewayping = channel.getJDA().getGatewayPing();

		channel.getJDA().getRestPing().queue( (time) ->
		channel.sendMessageFormat("Pong! %dm", time, gatewayping).queue()
				);
	}

}