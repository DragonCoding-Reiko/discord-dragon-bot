package de.dragonbot.listener.guild;

import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter{

	public void onGuildJoin(GuildJoinEvent event) {
		setDefault(event.getGuild());
	}

	private void setDefault(Guild guild) {
		//Setting DBs
		LiteSQL.newEntry("settings", 
				"guildid, deletemessage", 
				guild.getIdLong() + ", " + false);

		LiteSQL.newEntry("musicsettings", 
				"guildid, nowplaying", 
				guild.getIdLong() + ", " + false);

	}

}
