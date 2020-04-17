package de.dragonbot.music;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicUtil {

	public static void updateChannel(TextChannel channel) {
		ResultSet set = LiteSQL.getEntrys("*", 
				"musicchannel", 
				"guildid = " + channel.getGuild().getIdLong());

		try {
			if(set.next()) {
				LiteSQL.updateEntry("musicchannel", 
						"channelid = " + channel.getIdLong(), 
						"guildid = " + channel.getGuild().getIdLong());
			}
			else {
				LiteSQL.newEntry("musicchannel", 
						"guildid, channelid", 
						channel.getGuild().getIdLong() + ", " + channel.getIdLong());
			}
		} catch (SQLException e) { }
	}

	public static void sendEmbed(Long guildid, EmbedBuilder builder) {
		ResultSet set = LiteSQL.getEntrys("*", 
				"musicchannel", 
				"guildid = " + guildid);

		try {
			if(set.next()) {
				long channelid = set.getLong("channelid");

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
