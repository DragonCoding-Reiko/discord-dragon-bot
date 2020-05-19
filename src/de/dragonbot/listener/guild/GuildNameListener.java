package de.dragonbot.listener.guild;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildNameListener extends ListenerAdapter{

	
	@Override
	public void onGuildUpdateName(GuildUpdateNameEvent event){
	
		Long guild_ID = event.getGuild().getIdLong();
		String guild_Name = event.getNewName();
	
		String sql_UPDATE_GuildName = "UPDATE `Guilds` "
									+ "SET `guild_Name`='" + guild_Name + "'"
									+ "WHERE guild_ID = " + guild_ID;
		
		DragonBot.INSTANCE.listenerDB.execute(sql_UPDATE_GuildName);
	
	}
}
