package de.dragonbot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter{

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


			ResultSet set = LiteSQL.getEntrys("roleid", 
					"reactroles", 
					"guildid = " + guildid + " AND channelid = " + channelid + " AND messageid = " + messageID + " AND emote = '" + emote + "'");

			try {
				if(set.next()) {
					long roleid = set.getLong("roleid");

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
