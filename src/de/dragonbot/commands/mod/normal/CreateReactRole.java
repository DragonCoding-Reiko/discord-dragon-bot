package de.dragonbot.commands.mod.normal;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class CreateReactRole implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		if(args.length == 5 && !args[1].equalsIgnoreCase("remove")) {
			List<TextChannel> channels = message.getMentionedChannels();
			List<Role> roles = message.getMentionedRoles();
			List<Emote> emotes = message.getEmotes();


			if(!channels.isEmpty() && !roles.isEmpty()) {
				TextChannel tc = channels.get(0);
				Role role = roles.get(0);
				String messageIDString = args[2];

				try {
					Long messageID = Long.parseLong(messageIDString);

					if(!emotes.isEmpty()) {
						Emote emote = emotes.get(0);

						tc.addReactionById(messageID, emote).queue();

						DragonBot.INSTANCE.mainDB.newEntry("React_Roles", 
								"guild_ID, channel_ID, message_ID, emote, role_ID", 
								channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote.getId() + "', " + role.getIdLong());
					}
					else {
						String emote = args[3];

						tc.addReactionById(messageID, emote).queue();

						DragonBot.INSTANCE.mainDB.newEntry("React_Roles", 
								"guild_ID, channel_ID, message_ID, emote, role_ID", 
								channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote + "', " + role.getIdLong());
					}
				}
				catch (NumberFormatException e) { }
			}
		} else if (args.length == 5 && args[1].equalsIgnoreCase("remove")) {
			//#d reactrole remove <#channel> <messageID> <:emote:>

			List<TextChannel> channels = message.getMentionedChannels();
			List<Emote> emotes = message.getEmotes();

			if(!channels.isEmpty()) {
				TextChannel tc = channels.get(0);
				String messageIDString = args[3];

				try {
					Long messageID = Long.parseLong(messageIDString);

					if(!emotes.isEmpty() && !args[4].equalsIgnoreCase("all")) {

						if(!emotes.isEmpty()) {
							Emote emote = emotes.get(0);

							tc.removeReactionById(messageID, emote).queue();

							DragonBot.INSTANCE.mainDB.deleteEntry("React_Roles", 
									"guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + tc.getIdLong() + " AND message_ID = " + messageID + " AND emote = '" + emote.getId() + "'");
						}
						else {
							String emote = args[4];

							tc.removeReactionById(messageID, emote).queue();

							DragonBot.INSTANCE.mainDB.deleteEntry("React_Roles", 
									"guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + tc.getIdLong() + " AND message_ID = " + messageID + " AND emote = '" + emote + "'");
						}
					}
					else if (!emotes.isEmpty() && args[4].equalsIgnoreCase("all")){
						tc.clearReactionsById(messageID);

						DragonBot.INSTANCE.mainDB.deleteEntry("React_Roles", 
								"guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + tc.getIdLong() + " AND message_ID = " + messageID);
					}
				}
				catch (NumberFormatException e) { }
			}
		}
		else {
			channel.sendMessage("Bitte benutze `#d reactrole <#channel> <messageID> <:emote:> <@Rolle>`").complete().delete().queueAfter(20, TimeUnit.SECONDS);
		}
	}

}
