package de.dragonbot.manage;

public class SQLManager {

	public static void onCreate() {

		//Settings - Databases for several settings of a guild
		LiteSQL.newTable("settings", "id INTEGER", 
				"guildid INTEGER, deletemessage INTEGER");

		LiteSQL.newTable("musicsettings", "id INTEGER", 
				"guildid INTEGER, nowplaying INTEGER");


		//Roles - Databases to save the Command Roles
		LiteSQL.newTable("adminrole", "id INTEGER", 
				"guildid INTEGER, roleid INTEGER");

		LiteSQL.newTable("modrole", "id INTEGER", 
				"guildid INTEGER, roleid INTEGER");

		LiteSQL.newTable("djrole", "id INTEGER", 
				"guildid INTEGER, roleid INTEGER");

		LiteSQL.newTable("bdosettingsrole", "id INTEGER", 
				"guildid INTEGER, roleid INTEGER");

		LiteSQL.newTable("bdonoterole", "id INTEGER", 
				"guildid INTEGER, roleid INTEGER");

		LiteSQL.newTable("regularrole", "id INTEGER", 
				"guildid INTEGER, roleid INTEGER");


		//ModCommands - Databases for moderator commands
		LiteSQL.newTable("reactroles", "id INTEGER", 
				"guildid INTEGER, channelid INTEGER, messageid INTEGER, emote VARCHAR, roleid INTEGER");

		LiteSQL.newTable("statchannels", "id INTEGER", 
				"guildid INTEGER, categoryid INTEGER");

		LiteSQL.newTable("voicechannelhubs", "id INTEGER", 
				"guildid INTEGER, categoryid INTEGER, channelid INTEGER");


		//Music - Databases for music
		//Saves the channel, where music notifications are sent to
		LiteSQL.newTable("musicchannel", "id INTEGER", 
				"guildid INTEGER, channelid INTEGER");
		//Saves all the reactions and its function
		LiteSQL.newTable("dashboardreactions", "id INTEGER", 
				"guildid INTEGER, channelid INTEGER, messageid INTEGER, emote TEXT, action TEXT");
		//Saves the Channel for the Dashboard
		LiteSQL.newTable("npchannel", "id INTEGER", 
				"guildid INTEGER, channelid INTEGER");
		//Saves the Messages for the Dashboard
		LiteSQL.newTable("messages", "id INTEGER", 
				"guildid INTEGER, channelid INTEGER, msg1 INTEGER, msg2 INTEGER, msg3 INTEGER");
	}
}
