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
		DragonBot.INSTANCE.listenerDB.updateEntry("Guilds", "joined = " + false, "guild_ID = " + guild.getIdLong());
		
		DragonBot.INSTANCE.listenerDB.deleteEntry("Settings", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.listenerDB.deleteEntry("Music_Settings", "guild_ID = " + guild.getIdLong());

		//DragonBot.INSTANCE.listenerDB.deleteEntry("Admin_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.listenerDB.deleteEntry("Mod_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.listenerDB.deleteEntry("DJ_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.listenerDB.deleteEntry("BDO_Settings_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.listenerDB.deleteEntry("BDO_Note_Role", "guild_ID = " + guild.getIdLong());
		//DragonBot.INSTANCE.listenerDB.deleteEntry("Regular_Role", "guild_ID = " + guild.getIdLong());

		DragonBot.INSTANCE.listenerDB.deleteEntry("React_Roles", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.listenerDB.deleteEntry("Stats_Channels", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.listenerDB.deleteEntry("Voice_Channel_Hubs", "guild_ID = " + guild.getIdLong());

		DragonBot.INSTANCE.listenerDB.deleteEntry("Music_Channel", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.listenerDB.deleteEntry("Dashboard_Reactions", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.listenerDB.deleteEntry("Dashboard", "guild_ID = " + guild.getIdLong());
		DragonBot.INSTANCE.listenerDB.deleteEntry("Messages", "guild_ID = " + guild.getIdLong());
	}
	
}
