package de.dragonbot.listener;

import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoinListener extends ListenerAdapter{

	public void onGuildJoin(GuildJoinEvent event) {
		setDefault(event.getGuild());
	}

	public void onGuildLeave(GuildLeaveEvent event) {
		removeGuild(event.getGuild());
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

	private void removeGuild(Guild guild) {
		LiteSQL.deleteEntry("settings", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("musicsettings", "guildid = " + guild.getIdLong());

		LiteSQL.deleteEntry("adminrole", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("modrole", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("djrole", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("bdosettingsrole", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("bdonoterole", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("regular", "guildid = " + guild.getIdLong());

		LiteSQL.deleteEntry("reactroles", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("statchannels", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("voicechannelhubs", "guildid = " + guild.getIdLong());

		LiteSQL.deleteEntry("musicchannel", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("nowplayingchannel", "guildid = " + guild.getIdLong());
		LiteSQL.deleteEntry("npchannel", "guildid = " + guild.getIdLong());
	}
}
