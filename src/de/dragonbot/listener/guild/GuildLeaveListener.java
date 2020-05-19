package de.dragonbot.listener.guild;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeaveListener extends ListenerAdapter{

	public void onGuildLeave(GuildLeaveEvent event) {
		removeGuild(event.getGuild());
	}

	private void removeGuild(Guild guild) {
		
		String sql_UPDATE_GuildJoinedStatus = "UPDATE `Guilds` "
											+ "SET `joined`=" + false + " "
											+ "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_Settings = "DELETE FROM `Settings` "
								   + "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_MusicSettings = "DELETE FROM `Music_Settings` "
				   				        + "WHERE guild_ID = " + guild.getIdLong();
		
//		String sql_REMOVE_AdminRole = "DELETE FROM `Admin_Role` "
//				   				    + "WHERE guild_ID = " + guild.getIdLong();
//		
//		String sql_REMOVE_ModRole = "DELETE FROM `Mod_Role` "
//								  + "WHERE guild_ID = " + guild.getIdLong();
//		
//		String sql_REMOVE_DJRole = "DELETE FROM `DJ_Role` "
//								 + "WHERE guild_ID = " + guild.getIdLong();
//		
//		String sql_REMOVE_BDOSettingsRole = "DELETE FROM `BDO_Settings_Role` "
//				   				   		  + "WHERE guild_ID = " + guild.getIdLong();
//		
//		String sql_REMOVE_BDONoteRole = "DELETE FROM `BDO_Note_Role` "
//								      + "WHERE guild_ID = " + guild.getIdLong();
//		
//		String sql_REMOVE_RegularRole = "DELETE FROM `Regular_Role` "
//				   				      + "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_ReactRoles = "DELETE FROM `React_Roles` "
				   				     + "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_StatsChannels = "DELETE FROM `Stats_Channels` "
				   				        + "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_VCHub = "DELETE FROM `Voice_Channel_Hubs` "
				   				+ "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_MusicChannel = "DELETE FROM `Music_Channel` "
				   				       + "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_Dashboard = "DELETE FROM `Dashboard` "
								    + "WHERE guild_ID = " + guild.getIdLong();
		
		String sql_REMOVE_Messages = "DELETE FROM `Messages` "
				   				   + "WHERE guild_ID = " + guild.getIdLong();
		
		DragonBot.INSTANCE.listenerDB.execute(sql_UPDATE_GuildJoinedStatus);
		
		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_Settings);
		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_MusicSettings);

//		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_AdminRole);
//		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_ModRole);
//		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_DJRole);
//		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_BDOSettingsRole);
//		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_BDONoteRole);
//		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_RegularRole);

		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_ReactRoles);
		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_StatsChannels);
		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_VCHub);

		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_MusicChannel);
		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_Dashboard);
		DragonBot.INSTANCE.listenerDB.execute(sql_REMOVE_Messages);
	}
	
}
