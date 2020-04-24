package de.dragonbot.listener.guild;

import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeaveListener extends ListenerAdapter{

	public void onGuildLeave(GuildLeaveEvent event) {
		removeGuild(event.getGuild());
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
