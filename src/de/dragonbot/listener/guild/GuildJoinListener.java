package de.dragonbot.listener.guild;

import de.dragonbot.manage.MySQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter{

	public void onGuildJoin(GuildJoinEvent event) {
		setDefault(event.getGuild());
	}

	private void setDefault(Guild guild) {
		//Setting DBs
		MySQL.newEntry("Guilds", 
				"guild_ID, joined", 
				guild.getIdLong() + ", " + true);
		
		MySQL.newEntry("Settings", 
				"guild_ID, delete_Message", 
				guild.getIdLong() + ", " + false);

		MySQL.newEntry("Music_Settings", 
				"guild_ID, now_playing", 
				guild.getIdLong() + ", " + false);

	}

}
