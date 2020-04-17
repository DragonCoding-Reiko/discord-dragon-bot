package de.dragonbot.commands.mod.normal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.dragonbot.commands.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class Clear implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {

		if(m.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
			//message.delete().queue();
			String[] args = message.getContentDisplay().substring(subString).split(" ");


			if(args.length >= 1 ) {
				try {
					int amount = Integer.parseInt(args[1]);

					channel.purgeMessages(get(channel, amount));
					if (amount == 1) {
						channel.sendMessage("Es wurde "+ amount + " Nachricht gelöscht!").complete().delete().queueAfter(3, TimeUnit.SECONDS);
					} else {
						channel.sendMessage("Es wurden "+ amount + " Nachrichten gelöscht!").complete().delete().queueAfter(3, TimeUnit.SECONDS);
					}
					return;

				} catch (NumberFormatException e) {
					e.printStackTrace();			
				}
			}
		}
		else {
			channel.sendMessage("Du hast nicht die benötigte Berechtigung! (Manage Messages)").complete().delete().queueAfter(3, TimeUnit.SECONDS);
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
