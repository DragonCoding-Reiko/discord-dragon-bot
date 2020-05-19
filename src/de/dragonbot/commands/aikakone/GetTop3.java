package de.dragonbot.commands.aikakone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class GetTop3 implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		
		message.delete().queue();
		
		String sql_SELECT_PlayerScores = "SELECT playerName, score, date "
				   					   + "FROM highscores "
				   					   + "ORDER BY score DESC "
				   					   + "LIMIT 3";
		
		ResultSet set = DragonBot.INSTANCE.gameDB.getData(sql_SELECT_PlayerScores);
		EmbedBuilder builder = new EmbedBuilder();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY HH:mm");
		
		builder.setTitle("__**TOP 3 players by score are:**__");
		
		int i = 1;
		try {
			while(set.next()) {
				builder.addField("Top "+ i + ":", 
								 "Name: " + set.getString("playerName") + " \n "
							   + "Score: " + set.getInt("score") + " \n "
							   + "Date: " + format.format(set.getTimestamp("date")), 
								 false);
				
				i++;
			}
		} catch (SQLException e) { 
			Utils.printError(e, sql_SELECT_PlayerScores);
		}
		
		builder.addField("**Thank you!**", "Thanks to all who play our game :) \n Even if you're not on the TOP 3 keep going, maybe you will soon. ", false);
		
		Utils.sendEmbed(builder, channel, 10l, null);
	}
}
