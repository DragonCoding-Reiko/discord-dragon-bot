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

public class GetPlayerScores implements ServerCommand{

	
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();
		
		String[] args = message.getContentDisplay().substring(subString).split(" ");
		String playerName = args[1];
		int rows = 0;
		int totalScore = 0;
		
		String sql_SELECT_Stats = "SELECT COUNT(*), SUM(score) "
				    		    + "FROM highscores "
				    		    + "WHERE playerName = '" + playerName + "'";
		
		String sql_SELECT_Scores = "SELECT score, date "
			    			     + "FROM highscores "
			    			     + "WHERE playerName = '" + playerName + "'"
			    			     + "ORDER BY score DESC "
			    			     + "LIMIT 10";
		
		ResultSet set = DragonBot.INSTANCE.gameDB.getData(sql_SELECT_Stats);
		
		try {
			if(set.next()) 
				rows = set.getInt(1); 
				totalScore = set.getInt(2);
		} catch (SQLException e) {
			Utils.printError(e, sql_SELECT_Stats);
		}
		
		set = DragonBot.INSTANCE.gameDB.getData(sql_SELECT_Scores);
		EmbedBuilder builder = new EmbedBuilder();
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.YYYY HH:mm");
		builder.setTitle((rows == 0 ? "No Scores for the Player '" + playerName + "' found." : "__**TOP " + (rows > 10 ? "10" : rows) + " scores by '" + playerName + "' are:**__"));
		
		int i = 1;
		try {
			while(set.next()) {
				builder.addField(i + ". :",
								 "Score: " + set.getInt("score") + " \n "
							   + "Date: " + format.format(set.getTimestamp("date")), 
								 false);
				i++;
			}
		} catch (SQLException e) { 
			Utils.printError(e, sql_SELECT_Scores);
		}
		
		if(totalScore != 0) builder.addField("**Total Score: **", "" + totalScore, false);
		builder.addField("**Thank you!**", "Thanks for playing our game :) \n Even if you're not on the TOP 3 keep going, maybe you will soon. ", false);
		
		Utils.sendEmbed(builder, channel, 10l, null);
	}

}
