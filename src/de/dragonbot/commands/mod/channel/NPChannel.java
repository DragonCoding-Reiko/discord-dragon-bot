package de.dragonbot.commands.mod.channel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;

public class NPChannel implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		Guild guild = channel.getGuild();
		String[] args = message.getContentDisplay().substring(subString).split(" ");

		ResultSet set = LiteSQL.getEntrys("guildid, channelid", 
				"npchannel", 
				"guildid = " + guild.getIdLong());

		try {
			if(!set.next()) {
				if(args.length == 2) {
					String channelName = args[1];
					TextChannel createdChannel;

					createdChannel = guild.createTextChannel(channelName).complete();

					LiteSQL.updateEntry("musicsettings", 
							"nowplaying = " + true, 
							"guildid = " + guild.getIdLong());

					LiteSQL.newEntry("npchannel", 
							"guildid, channelid", 
							guild.getIdLong() + ", " + createdChannel.getIdLong());

					PermissionOverride override = new PermissionOverrideActionImpl(guild.getJDA(), createdChannel, createdChannel.getGuild().getPublicRole()).complete();
					PermissionOverride override2 = new PermissionOverrideActionImpl(guild.getJDA(), createdChannel, createdChannel.getGuild().getSelfMember()).complete();
					createdChannel.getManager()
					.putPermissionOverride(override.getPermissionHolder(), null, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION))
					.putPermissionOverride(override2.getPermissionHolder(),EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION) , null).queue();
				}
			}
			else if(args[1].equalsIgnoreCase("delete")) {
				TextChannel ch = guild.getTextChannelById(set.getLong("channelid"));

				LiteSQL.updateEntry("musicsettings", 
						"nowplaying = " + false, 
						"guildid = " + guild.getIdLong());

				LiteSQL.deleteEntry("npchannel", 
						"guildid = " + guild.getIdLong());

				ch.delete().complete();
			}
		} catch (SQLException e) { e.printStackTrace(); }
	}

}
