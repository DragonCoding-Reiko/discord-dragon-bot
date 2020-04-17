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

	public Message message1;
	public Message message2;
	public Message message3;

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		Guild guild = channel.getGuild();

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		LiteSQL.updateEntry("musicsettings", 
				"nowplaying = " + true, 
				"guildid = " + guild.getIdLong());

		ResultSet set = LiteSQL.getEntrys("guildid, channelid, messageid, emote, action", 
				"dashboardreactions", 
				"guildid = " + guild.getIdLong());
		try {
			if(!set.next()) {
				if(args.length == 2) {
					String channelName = args[1];
					TextChannel createdChannel;

					guild.createTextChannel(channelName).complete();
					createdChannel = guild.getTextChannelsByName(channelName, true).get(0);

					LiteSQL.newEntry("npchannel", 
							"guildid, channelid", 
							guild.getIdLong() + ", " + createdChannel.getIdLong());

					PermissionOverride override = new PermissionOverrideActionImpl(guild.getJDA(), createdChannel, createdChannel.getGuild().getPublicRole()).complete();
					PermissionOverride override2 = new PermissionOverrideActionImpl(guild.getJDA(), createdChannel, createdChannel.getGuild().getSelfMember()).complete();
					createdChannel.getManager()
					.putPermissionOverride(override.getPermissionHolder(), null, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION))
					.putPermissionOverride(override2.getPermissionHolder(),EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION) , null).queue();

					message1 = createdChannel.sendMessage("🔁QueueInitMessage").complete();
					message2 = createdChannel.sendMessage("⏸️NowPlayingInitMessage").complete();
					message3 = createdChannel.sendMessage("🔊VolumeInitMessage").complete();

					createdChannel.addReactionById(message2.getIdLong(), "🔀").queue();
					createdChannel.addReactionById(message2.getIdLong(), "⏹️ ").queue();
					createdChannel.addReactionById(message2.getIdLong(), "⏮️").queue();
					createdChannel.addReactionById(message2.getIdLong(), "⏸️").queue();
					createdChannel.addReactionById(message2.getIdLong(), "⏭️").queue();
					createdChannel.addReactionById(message2.getIdLong(), "🔁").queue();
					createdChannel.addReactionById(message2.getIdLong(), "🔂").queue();

					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'🔀'" + ", " + "'shuffle'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'⏹️'" + ", " + "'stop'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'⏮️ '" + ", " + "'last'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'⏸️  '" + ", " + "'pause'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'⏭️ '" + ", " + "'next'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'🔁  '" + ", " + "'loop'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message2.getIdLong() + ", " + "'🔂 '" + ", " + "'sloop'");

					createdChannel.addReactionById(message3.getIdLong(), "🔉").queue();
					createdChannel.addReactionById(message3.getIdLong(), "🔊").queue();
					createdChannel.addReactionById(message3.getIdLong(), "🔄").queue();

					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message3.getIdLong() + ", " + "'🔉 '" + ", " + "'voldown'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message3.getIdLong() + ", " + "'🔊 '" + ", " + "'volup'");
					LiteSQL.newEntry("dashboardreactions", 
							"guildid, channelid, messageid, emote, action", 
							guild.getIdLong() + ", " + createdChannel.getIdLong() + ", " + message3.getIdLong() + ", " + "'🔄 '" + ", " + "'volreset'");
				}
			}
			else if(args[1].equalsIgnoreCase("delete")) {

			}
		} catch (SQLException e) { e.printStackTrace(); }
	}

}
