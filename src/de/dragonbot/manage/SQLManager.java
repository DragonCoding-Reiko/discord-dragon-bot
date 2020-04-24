package de.dragonbot.manage;

public class SQLManager {

	public static void onCreate() {

		//Settings - Databases for several settings of a guild
		MySQL.newTable("Guilds", "ID INTEGER", 
				"guild_ID BIGINT, joined BOOLEAN");
		
		MySQL.newTable("Settings", "ID INTEGER", 
				"guild_ID BIGINT, delete_Message BOOLEAN");

		MySQL.newTable("Music_Settings", "ID INTEGER", 
				"guild_ID BIGINT, now_playing BOOLEAN");


		//Roles - Databases to save the Command Roles
		//MySQL.newTable("Admin_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//MySQL.newTable("Mod_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//MySQL.newTable("DJ_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//MySQL.newTable("BDO_Settings_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//MySQL.newTable("BDO_Note_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//MySQL.newTable("Regular_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");


		//ModCommands - Databases for moderator commands		
		MySQL.newTable("React_Roles", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT, message_ID BIGINT, emote TEXT, role_ID BIGINT");

		MySQL.newTable("Stats_Channels", "ID INTEGER", 
				"guild_ID BIGINT, category_ID BIGINT");

		MySQL.newTable("Voice_Channel_Hubs", "ID INTEGER", 
				"guild_ID BIGINT, category_ID BIGINT, channel_ID BIGINT");


		//Music - Databases for music
		//Saves the channel, where music notifications are sent to
		MySQL.newTable("Music_Channel", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT");
		//Saves all the reactions and its function
		MySQL.newTable("Dashboard_Reactions", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT, message_ID BIGINT, emote TEXT, action TEXT");
		//Saves the Channel for the Dashboard
		MySQL.newTable("Dashboard", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT");
		//Saves the Messages for the Dashboard
		MySQL.newTable("Messages", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT, message_ID_1 BIGINT, message_ID_2 BIGINT, message_ID_3 BIGINT");
	}
}
