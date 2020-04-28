package de.dragonbot.manage;

import de.dragonbot.DragonBot;

public class SQLManager {

	public static void onCreate() {

		//Settings - Databases for several settings of a guild
		DragonBot.INSTANCE.mainDB.newTable("Guilds", "ID INTEGER", 
				"guild_ID BIGINT, joined BOOLEAN");
		
		DragonBot.INSTANCE.mainDB.newTable("Settings", "ID INTEGER", 
				"guild_ID BIGINT, delete_Message BOOLEAN");

		DragonBot.INSTANCE.mainDB.newTable("Music_Settings", "ID INTEGER", 
				"guild_ID BIGINT, now_playing BOOLEAN");


		//Roles - Databases to save the Command Roles
		//DragonBot.INSTANCE.mainDB.newTable("Admin_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//DragonBot.INSTANCE.mainDB.newTable("Mod_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//DragonBot.INSTANCE.mainDB.newTable("DJ_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//DragonBot.INSTANCE.mainDB.newTable("BDO_Settings_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//DragonBot.INSTANCE.mainDB.newTable("BDO_Note_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");

		//DragonBot.INSTANCE.mainDB.newTable("Regular_Role", "ID INTEGER", 
		//		"guild_ID BIGINT, role_ID BIGINT");


		//ModCommands - Databases for moderator commands		
		DragonBot.INSTANCE.mainDB.newTable("React_Roles", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT, message_ID BIGINT, emote TEXT, role_ID BIGINT");

		DragonBot.INSTANCE.mainDB.newTable("Stats_Channels", "ID INTEGER", 
				"guild_ID BIGINT, category_ID BIGINT");

		DragonBot.INSTANCE.mainDB.newTable("Voice_Channel_Hubs", "ID INTEGER", 
				"guild_ID BIGINT, category_ID BIGINT, channel_ID BIGINT");


		//Music - Databases for music
		//Saves the channel, where music notifications are sent to
		DragonBot.INSTANCE.mainDB.newTable("Music_Channel", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT");
		//Saves all the reactions and its function
		DragonBot.INSTANCE.mainDB.newTable("Dashboard_Reactions", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT, message_ID BIGINT, emote TEXT, action TEXT");
		//Saves the Channel for the Dashboard
		DragonBot.INSTANCE.mainDB.newTable("Dashboard", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT");
		//Saves the Messages for the Dashboard
		DragonBot.INSTANCE.mainDB.newTable("Messages", "ID INTEGER", 
				"guild_ID BIGINT, channel_ID BIGINT, message_ID_1 BIGINT, message_ID_2 BIGINT, message_ID_3 BIGINT");
	}
}
