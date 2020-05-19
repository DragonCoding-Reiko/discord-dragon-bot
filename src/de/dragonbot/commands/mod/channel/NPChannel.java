package de.dragonbot.commands.mod.channel;

import java.util.EnumSet;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.MusicDashboard;
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

		TextChannel dashboard = Utils.getDashboardChannel(guild);

		if(dashboard == null) {
			if(args.length == 2) {
				String channelName = args[1];
				TextChannel createdChannel;
	
				createdChannel = guild.createTextChannel(channelName).complete();
	
				String sql_UPDATE_NowPlaying = "UPDATE `Music_Settings` "
											+ "SET `now_playing`= " + true 
											+ " WHERE guild_ID= " + guild.getIdLong();
				
				String sql_INSERT_NewDashboard = "INSERT INTO `Dashboard`(`guild_ID`, `channel_ID`) "
											 + "VALUES (" + guild.getIdLong() + ", " + createdChannel.getIdLong() + ")";
				
				DragonBot.INSTANCE.mainDB.execute(sql_UPDATE_NowPlaying);
	
				DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewDashboard);
	
				PermissionOverride override = new PermissionOverrideActionImpl(guild.getJDA(), createdChannel, createdChannel.getGuild().getPublicRole()).complete();
				PermissionOverride override2 = new PermissionOverrideActionImpl(guild.getJDA(), createdChannel, createdChannel.getGuild().getSelfMember()).complete();
				createdChannel.getManager()
				.putPermissionOverride(override.getPermissionHolder(), null, EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION))
				.putPermissionOverride(override2.getPermissionHolder(),EnumSet.of(Permission.MESSAGE_WRITE, Permission.MESSAGE_TTS, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ADD_REACTION) , null).queue();
				
				MusicDashboard.onAFK(createdChannel);
			}
		}
		else if(args[1].equalsIgnoreCase("delete")) {
	
			String sql_UPDATE_NowPlaying = "UPDATE `Music_Settings` "
						+ "SET `now_playing`= " + false 
						+ " WHERE guild_ID= " + guild.getIdLong();
		
			String sql_REMOVE_Dashboard = "DELETE FROM `Dashboard` "
						+ "WHERE guild_ID = " + guild.getIdLong();
			
			DragonBot.INSTANCE.mainDB.execute(sql_UPDATE_NowPlaying);
	
			DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_Dashboard);
	
			dashboard.delete().complete();
		}
	}

}
