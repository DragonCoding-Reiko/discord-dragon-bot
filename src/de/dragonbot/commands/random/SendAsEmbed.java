package de.dragonbot.commands.random;

import de.dragonbot.commands.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SendAsEmbed implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();
		
		String mess = message.getContentRaw().substring(subString).substring(6);

		sendEmbed(channel, mess);
	}

	public static void sendEmbed(TextChannel channel, String message) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setDescription(message);
		builder.setColor(0xeb974e);

		channel.sendMessage(builder.build()).queue();
	}

}