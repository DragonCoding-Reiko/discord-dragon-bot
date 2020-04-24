package de.dragonbot.listener.guild;

import de.dragonbot.manage.MySQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeaveListener extends ListenerAdapter{

	public void onGuildLeave(GuildLeaveEvent event) {
		removeGuild(event.getGuild());
	}

	private void removeGuild(Guild guild) {
		MySQL.updateEntry("Guilds", "joined = " + false, "guild_ID = " + guild.getIdLong());
		
		MySQL.deleteEntry("Settings", "guild_ID = " + guild.getIdLong());
		MySQL.deleteEntry("Music_Settings", "guild_ID = " + guild.getIdLong());

		//MySQL.deleteEntry("Admin_Role", "guild_ID = " + guild.getIdLong());
		//MySQL.deleteEntry("Mod_Role", "guild_ID = " + guild.getIdLong());
		//MySQL.deleteEntry("DJ_Role", "guild_ID = " + guild.getIdLong());
		//MySQL.deleteEntry("BDO_Settings_Role", "guild_ID = " + guild.getIdLong());
		//MySQL.deleteEntry("BDO_Note_Role", "guild_ID = " + guild.getIdLong());
		//MySQL.deleteEntry("Regular_Role", "guild_ID = " + guild.getIdLong());

		MySQL.deleteEntry("React_Roles", "guild_ID = " + guild.getIdLong());
		MySQL.deleteEntry("Stats_Channels", "guild_ID = " + guild.getIdLong());
		MySQL.deleteEntry("Voice_Channel_Hubs", "guild_ID = " + guild.getIdLong());

		MySQL.deleteEntry("Music_Channel", "guild_ID = " + guild.getIdLong());
		MySQL.deleteEntry("Dashboard_Reactions", "guild_ID = " + guild.getIdLong());
		MySQL.deleteEntry("Dashboard", "guild_ID = " + guild.getIdLong());
		MySQL.deleteEntry("Messages", "guild_ID = " + guild.getIdLong());
	}
	
}
