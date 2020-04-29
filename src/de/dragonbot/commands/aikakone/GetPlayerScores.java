package de.dragonbot.commands.aikakone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
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
		
		String sql = "SELECT COUNT(*) "
				   + "FROM highscores "
				   + "WHERE playerName = \"" + playerName + "\"";
		
		ResultSet set = DragonBot.INSTANCE.gameDB.execute(sql);
		
		try {
			if(set.next()) rows = set.getInt(1);
		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
		}
		
		sql = "SELECT SUM(score) "
				   + "FROM highscores "
				   + "WHERE playerName = \"" + playerName + "\"";
		
		set = DragonBot.INSTANCE.gameDB.execute(sql);
		
		try {
			if(set.next()) totalScore = set.getInt(1);
		} catch (SQLException e) {
			System.out.println(sql);
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
		}
		
		sql = "SELECT score, date "
				   + "FROM highscores "
				   + "WHERE playerName = \"" + playerName + "\" "
				   + "ORDER BY score DESC "
				   + "LIMIT 10";
		
		set = DragonBot.INSTANCE.gameDB.execute(sql);
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
			System.out.println(sql);
			System.out.println(e.getMessage());
			System.out.println(e.getLocalizedMessage());
		}
		
		if(totalScore != 0) builder.addField("**Total Score: **", "" + totalScore, false);
		builder.addField("**Thank you!**", "Thanks for playing our game :) \n Even if you're not on the TOP 3 keep going, maybe you will soon. ", false);
		
		channel.sendMessage(builder.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
	}

}
