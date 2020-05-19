package de.dragonbot.listener.reactrole;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.dragonbot.DragonBot;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactRoleAddListener extends ListenerAdapter{

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		String emote = "";
		if(event.getReactionEmote().isEmoji()) {
			emote = event.getReactionEmote().getEmoji();
		}
		else {
			emote = event.getReactionEmote().getId();
		}
		onReaction(event.getUser(), event.getGuild(), event.getTextChannel(), event.getMessageIdLong(), emote, event.getMember(), "AddEvent");
	}

	public void onReaction(User user, Guild guild, TextChannel channel, Long messageID, String emote, Member member, String type) {
		if(!user.isBot()) {

			long guildid = guild.getIdLong();
			long channelid = channel.getIdLong();

			String sql_SELECT_ReactRole = "SELECT `role_ID` "
										+ "FROM `React_Roles` "
										+ "WHERE guild_ID = " + guildid + " AND channel_ID = " + channelid + " AND message_ID = " + messageID + " AND emote = '" + emote + "'";

			ResultSet set = DragonBot.INSTANCE.listenerDB.getData(sql_SELECT_ReactRole);

			try {
				if(set.next()) {
					long roleid = set.getLong("role_ID");

					if(type.equalsIgnoreCase("AddEvent")) {
						guild.addRoleToMember(member, guild.getRoleById(roleid)).queue();
					} else if (type.equalsIgnoreCase("RemoveEvent")) {
						guild.removeRoleFromMember(member, guild.getRoleById(roleid)).queue();
					}
				}
			} catch (SQLException e) {
				Utils.printError(e, null);
			}
		}
	}
}
