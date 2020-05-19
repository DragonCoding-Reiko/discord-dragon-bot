package de.dragonbot.commands.random;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Invite implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int substring) {
		message.delete().queue();

		Utils.sendEmbed("INVITE ME", "Lad mich zu deinem Discord ein^^ \n" + "**Link: **" + DragonBot.INSTANCE.link, channel, 20l, null);
	}

}
