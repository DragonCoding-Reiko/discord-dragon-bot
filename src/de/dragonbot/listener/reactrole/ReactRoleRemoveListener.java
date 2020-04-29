package de.dragonbot.listener.reactrole;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactRoleRemoveListener extends ListenerAdapter{

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		String emote = "";
		if(event.getReactionEmote().isEmoji()) {
			emote = event.getReactionEmote().getEmoji();
		}
		else {
			emote = event.getReactionEmote().getId();
		}
		onReaction(event.getUser(), event.getGuild(), event.getTextChannel(), event.getMessageIdLong(), emote, event.getMember(), "RemoveEvent");
	}

	public void onReaction(User user, Guild guild, TextChannel channel, Long messageID, String emote, Member member, String type) {
		if(!user.isBot()) {

			long guildid = guild.getIdLong();
			long channelid = channel.getIdLong();


			ResultSet set = DragonBot.INSTANCE.listenerDB.getEntrys("role_ID", 
					"React_Roles", 
					"guild_ID = " + guildid + " AND channel_ID = " + channelid + " AND message_ID = " + messageID + " AND emote = '" + emote + "'");

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
				e.printStackTrace();
			}
		}
	}
	
}
