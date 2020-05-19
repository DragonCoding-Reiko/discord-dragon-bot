package de.dragonbot.manage;

import de.dragonbot.DragonBot;

public class SQLManager {

	public static void onCreate() {

		String sql_CREATE_Guilds = "CREATE TABLE IF NOT EXISTS `Guilds` "
								 + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
								 + "`guild_Name` TEXT, `guild_ID` BIGINT, `joined` BOOLEAN, "
								 + "PRIMARY KEY (`ID`))";
					
		String sql_CREATE_Settings = "CREATE TABLE IF NOT EXISTS `Settings` "
				 				   + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
				 				   + "`guild_ID` BIGINT, `delete_Message` BOOLEAN,"
				 				   + "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_MusicSettings = "CREATE TABLE IF NOT EXISTS `Music_Settings` "
									    + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
									    + "`guild_ID` BIGINT, `now_playing` BOOLEAN, "
									    + "PRIMARY KEY (`ID`))";
		
//		String sql_CREATE_AdminRole = "CREATE TABLE IF NOT EXISTS `Admin_Role` "
//								    + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
//								    + "`guild_ID` BIGINT, `role_ID` BIGINT, "
//									+ "PRIMARY KEY (`ID`))";
//		
//		String sql_CREATE_ModRole = "CREATE TABLE IF NOT EXISTS `Mod_Role` "
//								  + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
//								  + "`guild_ID` BIGINT, `role_ID` BIGINT, "
//                                + "PRIMARY KEY (`ID`))";
//		
//		String sql_CREATE_DJRole = "CREATE TABLE IF NOT EXISTS `DJ_Role` "
//								 + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
//								 + "`guild_ID` BIGINT, `role_ID` BIGINT, "
//								 + "PRIMARY KEY (`ID`))";
//		
//		String sql_CREATE_BDOSettingsRole = "CREATE TABLE IF NOT EXISTS `BDO_Settings_Role` "
//										  + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
//										  + "`guild_ID` BIGINT, `role_ID` BIGINT, "
//										  + "PRIMARY KEY (`ID`))";
//		
//		String sql_CREATE_BDONoteRole =	"CREATE TABLE IF NOT EXISTS `BDO_Note_Role` "
//									  + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
//									  + "`guild_ID` BIGINT, `role_ID` BIGINT, "
//									  + "PRIMARY KEY (`ID`))";
//
//		String sql_CREATE_RegularRole =	"CREATE TABLE IF NOT EXISTS `Regular_Role` "
//									  + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
//									  + "`guild_ID` BIGINT, `role_ID` BIGINT, "
//									  + "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_ReactRoles = "CREATE TABLE IF NOT EXISTS `React_Roles` "
									 + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
									 + "`guild_ID` BIGINT, `channel_ID` BIGINT, `message_ID` BIGINT, `emote` TEXT, `role_ID` BIGINT, " 
									 + "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_StatsChannels = "CREATE TABLE IF NOT EXISTS `Stats_Channels` "
									    + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
									    + "`guild_ID` BIGINT, `category_ID` BIGINT, "
									    + "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_VoiceChannelHubs = "CREATE TABLE IF NOT EXISTS `Voice_Channel_Hubs` "
									       + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
									       + "`guild_ID` BIGINT, `category_ID` BIGINT, `channel_ID` BIGINT, "
										   + "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_MusicChannel = "CREATE TABLE IF NOT EXISTS `Music_Channel` "
									   + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
									   + "`guild_ID` BIGINT, `channel_ID` BIGINT, "
									   + "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_Playlists = "CREATE TABLE IF NOT EXISTS `Playlists` "
				   					+ "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
				   					+ "`guild_ID` BIGINT, `creator_ID` BIGINT, `playlist_Name` TEXT, "
				   					+ "PRIMARY KEY (`ID`))";

		String sql_CREATE_Dashboard = "CREATE TABLE IF NOT EXISTS `Dashboard` "
									+ "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
									+ "`guild_ID` BIGINT, `channel_ID` BIGINT, "
									+ "PRIMARY KEY (`ID`))";
		
		String sql_CREATE_Messages = "CREATE TABLE IF NOT EXISTS `Messages` "
								   + "(`ID` INTEGER(11) NOT NULL AUTO_INCREMENT, "
								   + "`guild_ID` BIGINT, `channel_ID` BIGINT, `message_ID_1` BIGINT, `message_ID_2` BIGINT, `message_ID_3` BIGINT, "
								   + "PRIMARY KEY (`ID`))";
		
		//Settings - Databases for several settings of a guild
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_Guilds);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_Settings);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_MusicSettings);


		//Roles - Databases to save the Command Roles
//		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_AdminRole);
//		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_ModRole);
//		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_DJRole);
//		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_BDOSettingsRole);
//		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_BDONoteRole);
//		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_RegularRole);


		//ModCommands - Databases for moderator commands		
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_ReactRoles);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_StatsChannels);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_VoiceChannelHubs);


		//Music - Databases for music
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_MusicChannel);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_Playlists);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_Dashboard);
		DragonBot.INSTANCE.mainDB.execute(sql_CREATE_Messages);
	}
}
