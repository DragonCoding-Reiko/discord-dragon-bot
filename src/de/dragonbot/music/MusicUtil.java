package de.dragonbot.music;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import de.dragonbot.manage.MySQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicUtil {

	public static void updateChannel(TextChannel channel) {
		ResultSet set = MySQL.getEntrys("*", 
				"Music_Channel", 
				"guild_ID = " + channel.getGuild().getIdLong());

		try {
			if(set.next()) {
				MySQL.updateEntry("Music_Channel", 
						"channel_ID = " + channel.getIdLong(), 
						"guild_ID = " + channel.getGuild().getIdLong());
			}
			else {
				MySQL.newEntry("Music_Channel", 
						"guild_ID, channel_ID", 
						channel.getGuild().getIdLong() + ", " + channel.getIdLong());
			}
		} catch (SQLException e) { }
	}

	public static void sendEmbed(Long guildid, EmbedBuilder builder) {
		ResultSet set = MySQL.getEntrys("*", 
				"Music_Channel", 
				"guild_ID = " + guildid);

		try {
			if(set.next()) {
				long channelid = set.getLong("channel_ID");

				Guild guild;
				if((guild = DragonBot.INSTANCE.shardMan.getGuildById(guildid)) != null) {
					TextChannel channel;
					if((channel = guild.getTextChannelById(channelid)) != null) {
						channel.sendMessage(builder.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
					}
				}

			}
		} catch (SQLException e) { }
	}
}
