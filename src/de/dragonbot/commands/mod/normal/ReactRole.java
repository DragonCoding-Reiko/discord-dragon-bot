package de.dragonbot.commands.mod.normal;

import java.awt.Color;
import java.util.List;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReactRole implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		if(args.length == 5 && !args[1].equalsIgnoreCase("remove")) {
			//#d reactrole <#channel> <messageID> <:emote:> <@Rolle>
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

						String sql_INSERT_NewReactRole = "INSERT INTO `React_Roles`(`guild_ID`, `channel_ID`, `message_ID`, `emote`, `role_ID`) "
													   + "VALUES (" + channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote.getId() + "', " + role.getIdLong() + ")";
						
						DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewReactRole);
					}
					else {
						String emote = args[3];

						tc.addReactionById(messageID, emote).queue();

						String sql_INSERT_NewReactRole = "INSERT INTO `React_Roles`(`guild_ID`, `channel_ID`, `message_ID`, `emote`, `role_ID`) "
								   + "VALUES (" + channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + messageID + ", '" + emote + "', " + role.getIdLong() + ")";
	
						DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewReactRole);
					}
				}
				catch (NumberFormatException e) { 
					Utils.printError(e, null);
				}
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
							Emote emote = emotes.get(0);

							tc.removeReactionById(messageID, emote).queue();

							String sql_REMOVE_ReactRole = "DELETE FROM `React_Roles` "
														+ "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + tc.getIdLong() + " AND message_ID = " + messageID + " AND emote = '" + emote.getId() + "'";
							
							DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_ReactRole);
					}
					else if(emotes.isEmpty() && !args[4].equalsIgnoreCase("all")) {
						String emote = args[4];

						tc.removeReactionById(messageID, emote).queue();

						String sql_REMOVE_ReactRole = "DELETE FROM `React_Roles` "
													+ "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + tc.getIdLong() + " AND message_ID = " + messageID + " AND emote = '" + emote + "'";
						
						DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_ReactRole);
					}
					else if (emotes.isEmpty() && args[4].equalsIgnoreCase("all")){
						tc.clearReactionsById(messageID).queue();

						String sql_REMOVE_ReactRole = "DELETE FROM `React_Roles` "
													+ "WHERE guild_ID = " + channel.getGuild().getIdLong() + " AND channel_ID = " + tc.getIdLong() + " AND message_ID = " + messageID;
	
						DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_ReactRole);
					}
				}
				catch (NumberFormatException e) { 
					Utils.printError(e, null);
				}
			}
		}
		else {
			Utils.sendEmbed("ERROR", "Syntax falsch! \n" + "Bitte benutze `#d reactrole <#channel> <messageID> <:emote:> <@Rolle>`", channel, 20, new Color(0xff0000));
		}
	}

}
