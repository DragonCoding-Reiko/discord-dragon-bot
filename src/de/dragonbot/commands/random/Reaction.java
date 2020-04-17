package de.dragonbot.commands.random;

import java.util.ArrayList;
import java.util.List;

import de.dragonbot.commands.ServerCommand;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Reaction implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		//arg[0]  arg[1]   arg[2]   arg[3]
		//#d     reaction channel 45634563456 :ok:

		String[] args = message.getContentDisplay().substring(subString).split(" ");
		List<TextChannel> channels = message.getMentionedChannels();
		List<Emote> emotes = message.getEmotes();

		if(!channels.isEmpty()) {
			TextChannel tc = message.getMentionedChannels().get(0);
			String messageIDString = args[2];

			try {
				Long messageID = Long.parseLong(messageIDString);
				List<String> customEmotes = new ArrayList<>();

				for(Emote emote : emotes) {
					tc.addReactionById(messageID, emote).queue();
					customEmotes.add(":" + emote.getName() + ":");
				}

				for(int i = 3; i < args.length; i++) {
					String emote = args[i];

					if(!customEmotes.contains(emote)) {
						tc.addReactionById(messageID, args[i]).queue();
					}

				}
			}
			catch (NumberFormatException e) { }
		}
	}

}

