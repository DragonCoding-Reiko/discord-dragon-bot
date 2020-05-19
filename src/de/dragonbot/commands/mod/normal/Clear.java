package de.dragonbot.commands.mod.normal;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Clear implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {

		if(m.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
			String[] args = message.getContentDisplay().substring(subString).split(" ");


			if(args.length >= 1 ) {
				try {
					int amount = Integer.parseInt(args[1]);

					channel.purgeMessages(get(channel, amount));
					if (amount == 1) {
						Utils.sendEmbed("Info", "Es wurde "+ amount + " Nachricht gelöscht!", channel, 3l, null);
					} else {
						Utils.sendEmbed("Info", "Es wurden "+ amount + " Nachrichten gelöscht!", channel, 3l, null);
					}
					return;

				} catch (NumberFormatException e) {
					Utils.printError(e, null);			
				}
			}
		}
		else {
			Utils.sendEmbed("ERROR", "Du hast nicht die benötigte Berechtigung! (Manage Messages)", channel, 3l, new Color(0xff0000));
		}
	}

	public List<Message> get(MessageChannel channel, int amount) {
		List<Message> messages = new ArrayList<>();
		int i = amount + 1;

		for(Message message : channel.getIterableHistory().cache(false)) {

			if(!message.isPinned()) {
				messages.add(message);
			}
			if(--i <= 0) break;
		}

		return messages;
	}

}
