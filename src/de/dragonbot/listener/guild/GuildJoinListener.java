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
		String sql_INSERT_NewGuild = "INSERT INTO `Guilds`(`guild_Name`, `guild_ID`, `joined`) "
								   + "VALUES ('" + guild.getName() +  "', " + guild.getIdLong() + ", " + true + ")";
		
		String sql_INSERT_NewSettings = "INSERT INTO `Settings`(`guild_ID`, `delete_Message`) "
									  + "VALUES (" + guild.getIdLong() + ", " + false + ")";
		
		String sql_INSERT_NewMusicSettings = "INSERT INTO `Music_Settings`(`guild_ID`, `now_playing`) "
										   + "VALUES (" + guild.getIdLong() + ", " + false + ")";
		
		DragonBot.INSTANCE.listenerDB.execute(sql_INSERT_NewGuild);
		
		DragonBot.INSTANCE.listenerDB.execute(sql_INSERT_NewSettings);

		DragonBot.INSTANCE.listenerDB.execute(sql_INSERT_NewMusicSettings);

	}

}
