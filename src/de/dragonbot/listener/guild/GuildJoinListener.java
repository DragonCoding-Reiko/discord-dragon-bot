package de.dragonbot.listener.guild;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter{

	public void onGuildJoin(GuildJoinEvent event) {
		setDefault(event.getGuild());
	}

	private void setDefault(Guild guild) {
		//Setting DBs
		DragonBot.INSTANCE.listenerDB.newEntry("Guilds", 
				"guild_Name, guild_ID, joined", 
				guild.getName() + ", " + guild.getIdLong() + ", " + true);
		
		DragonBot.INSTANCE.listenerDB.newEntry("Settings", 
				"guild_ID, delete_Message", 
				guild.getIdLong() + ", " + false);

		DragonBot.INSTANCE.listenerDB.newEntry("Music_Settings", 
				"guild_ID, now_playing", 
				guild.getIdLong() + ", " + false);

	}

}
