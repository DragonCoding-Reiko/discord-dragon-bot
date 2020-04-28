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
		DragonBot.INSTANCE.mainDB.updateEntry("Guilds", "joined = " + false, "guild_ID = " + guild.getIdLong());
		
		DragonBot.INSTANCE.mainDB.deleteEntry("Settings", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.mainDB.deleteEntry("Music_Settings", "guild_ID = " + guild.getIdLong());

		//DragonBot.INSTANCE.mainDB.deleteEntry("Admin_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.mainDB.deleteEntry("Mod_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.mainDB.deleteEntry("DJ_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.mainDB.deleteEntry("BDO_Settings_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.mainDB.deleteEntry("BDO_Note_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.mainDB.deleteEntry("Regular_Role", "guild_ID = " + guild.getIdLong());

		DragonBot.INSTANCE.mainDB.deleteEntry("React_Roles", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.mainDB.deleteEntry("Stats_Channels", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.mainDB.deleteEntry("Voice_Channel_Hubs", "guild_ID = " + guild.getIdLong());

		DragonBot.INSTANCE.mainDB.deleteEntry("Music_Channel", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.mainDB.deleteEntry("Dashboard_Reactions", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.mainDB.deleteEntry("Dashboard", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.mainDB.deleteEntry("Messages", "guild_ID = " + guild.getIdLong());
	}
	
}
