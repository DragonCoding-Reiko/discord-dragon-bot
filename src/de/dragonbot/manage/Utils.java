package de.dragonbot.manage;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Utils {

	/**
	* Sends a EmbedMessage to the designated channel.
	* Uses provided Strings to build it.
	* 
	* <p>
	* @param title <p>Sets the Title of the EmbedMessage.
	* 
	* @param description <p>Sets the Description of the EmbedMessage.
	* 
	* @param channel <p>The Channel the EmbedMessage should be sent to.
	* 
	* @param deleteAfter <p>(Nullable) Time (in Seconds, Long) after which the sent message should be deleted.
	* 					 <p> If '0' it won't be deleted.
	* 
	* @param color <p>(Nullable) The EmbedMessages Blocks Color. 
	* 			   <p>Use 'new Color(Hexcode)' or 'new Color(RGB Code)'
	* 
	* <p>
	* @since 15-05-2020
	* @version 1.0
	* @author DragonCoder
	*/
	public static void sendEmbed(String title, String description, TextChannel channel, long deleteAfter, @Nullable Color color) {
		color = Optional.ofNullable(color).orElseGet(() -> new Color(0x09a3eb));
		
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle(title);
		builder.setDescription(description);
		
		builder.setColor(color);
		
		if(deleteAfter == 0) {
			channel.sendMessage(builder.build()).queue();
		}
		else {
			channel.sendMessage(builder.build()).complete().delete().queueAfter(deleteAfter, TimeUnit.SECONDS);
		}
		
	}
	/**
	* Sends a EmbedMessage to the designated channel.
	* Uses a provided EmbedBuilder to build it.
	* 
	* <p>
	* @param builder <p>An finished but not built EmbedBuilder.
	* 
	* @param channel <p>The Channel the EmbedMessage should be sent to.
	*
	* @param deleteAfter <p>(Nullable) Time (in Seconds, Long) after which the sent message should be deleted.
	* 					 <p> If '0' it wont be deleted.
	* 
	* @param color <p>(Optional) The EmbedMessages Blocks Color. 
	* 			   <p>Use 'new Color(Hexcode)' or 'new Color(RGB Code)'
	* 
	* <p>
	* @since 15-05-2020
	* @version 1.0
	* @author DragonCoder
	*/
	public static void sendEmbed(EmbedBuilder builder, TextChannel channel, @Nullable long deleteAfter, @Nullable Color color) {
		color = Optional.ofNullable(color).orElseGet(() -> new Color(0x09a3eb));
		deleteAfter = Optional.ofNullable(deleteAfter).orElseGet(() -> 0l);
		
		builder.setColor(color);
		
		if(deleteAfter == 0) {
			channel.sendMessage(builder.build()).queue();
		}
		else {
			channel.sendMessage(builder.build()).complete().delete().queueAfter(deleteAfter, TimeUnit.SECONDS);
		}
	}

	
	/**
	* Prints the Exception Messages in a more readable Form and also saves them to an external file.
	* Plus it gives them some additional informations.
	* 
	* <p>
	* @param exception <p>The exception.
	* @param arg       <p>(Optional) An argument you want to print. E.g. the SQL String.
	* 
	* <p>
	* @since 16-05-2020
	* @version 1.0
	* @author DragonCoder
	*/
	public static void printError(Throwable exception, @Nullable String arg) {		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy -> HH:mm:ss.SSS");
		StackTraceElement stackTraceThrower = exception.getStackTrace()[0];
		StackTraceElement stackTraceCaller = exception.getStackTrace()[1];
		
		String timestamp = dateFormat.format(Calendar.getInstance().getTime());
		String argument = arg;
		String errorMessage = "    Message: " + exception.getMessage();
		String errorThrower = "Thrower: \n" + "    Class: " + stackTraceThrower.getClassName() + "  ---  Method: " + stackTraceThrower.getMethodName() + "  ---  Line: " + stackTraceThrower.getLineNumber();
		String errorCaller = "Caller: \n" + "    Class: " + stackTraceCaller.getClassName() + "  ---  Method: " + stackTraceCaller.getMethodName() + "  ---  Line: " + stackTraceCaller.getLineNumber();
		
		String outputMessageShort = "---------------------------------------------------------------------------------------------------- \n"
							 	  + timestamp + " \n" 
							 	  + (argument == null ? "" : "    Argument: " + argument + " \n")
							 	  + errorMessage + " \n"
							 	  + errorThrower + " \n"
							 	  + errorCaller + " \n";
		
		String outputMessageLong = "---------------------------------------------------------------------------------------------------- \n"
								 + timestamp + " \n"
								 + (argument == null ? "" : "    Argument: " + argument + " \n")
								 + exception.getStackTrace() + " \n";
		
		File errorLogShort = new File("./errorLogShort.txt");
		File errorLogLong = new File("./errorLogLong.txt");
	
		FileWriter writer;
		try {
			writer = new FileWriter(errorLogShort, true);			
			writer.write(outputMessageShort);
			writer.flush();
	    	
			writer.close();
			
			writer = new FileWriter(errorLogLong, true);			
			writer.write(outputMessageLong);
			writer.flush();
			
			writer.close();
		} catch (IOException e) {
			dateFormat.format(Calendar.getInstance().getTime());
			System.out.println("");
			e.printStackTrace();
		}
		
		System.out.println(outputMessageShort);
		
	}
	
	
	/**
	* Gets the dashboard channel of a guild if existing.
	* 
	* <p>
	* @param guild <p>The guild which's dashboard should be found.
	* 
	* <p>
	* @since 15-05-2020
	* @version 1.0
	* @author DragonCoder
	*/
	public static TextChannel getDashboardChannel(Guild guild) {
		long channel_ID = 0l;
		TextChannel channel;
		
		String sql_SELECT_Dashboard = "SELECT `channel_ID` "
									+ "FROM `Dashboard` "
									+ "WHERE guild_ID = " + guild.getIdLong();
		
		ResultSet set = DragonBot.INSTANCE.listenerDB.getData(sql_SELECT_Dashboard);
		
		try {
			if(set.next()) {
				channel_ID = set.getLong("channel_ID");
				channel = guild.getTextChannelById(channel_ID);
				
				return channel;
			}
		} catch (SQLException e) { 
			 Utils.printError(e, null);
		}
		
		return null;
	}

	/**
	* Gets the default music channel of a guild.
	* (The channel, where now playing, etc. should be sent to.)
	* 
	* <p>
	* @param guild_ID <p>The (long) guild_ID of the guild.
	* 
	* <p>
	* @since 15-05-2020
	* @version 1.0
	* @author DragonCoder
	*/
	public static TextChannel getMusicChannel(Long guild_ID) {
		Guild guild = DragonBot.INSTANCE.getShardMan().getGuildById(guild_ID);
		long channel_ID = 0l;
		TextChannel channel;
		
		String sql_SELECT_MusicChannel = "SELECT `channel_ID` "
									   + "FROM `Music_Channel` "
									   + "WHERE guild_ID = " + guild_ID;

		ResultSet set = DragonBot.INSTANCE.listenerDB.getData(sql_SELECT_MusicChannel);

		try {
			if(set.next()) {
				channel_ID = set.getLong("channel_ID");
				channel = guild.getTextChannelById(channel_ID);
				
				return channel;
			}
		} catch (SQLException e) { 
			Utils.printError(e, null);
		}
		
		return null;
	}
	
	
	/**
	* Sets the default music channel of a guild.
	* (The channel, where now playing, etc. should be sent to.)
	* 
	* <p>
	* @param channel <p>The channel where the default music messages are sent to.
	* 
	* <p>
	* @since 15-05-2020
	* @version 1.0
	* @author DragonCoder
	*/
	public static void setMusicChannel(TextChannel channel) {
		String sql_SELECT_MusicChannel = "SELECT `channel_ID` "
				   					   + "FROM `Music_Channel` "
				   					   + "WHERE guild_ID = " + channel.getGuild().getIdLong();

		ResultSet set = DragonBot.INSTANCE.listenerDB.getData(sql_SELECT_MusicChannel);

		try {
			if(set.next()) {
				String sql_UPDATE_MusicChannel = "UPDATE `Music_Channel` "
											   + "SET `channel_ID`=`" + channel.getIdLong() + "`"
											   + "WHERE guild_ID = " + channel.getGuild().getIdLong();
				
				DragonBot.INSTANCE.mainDB.execute(sql_UPDATE_MusicChannel);
			}
			else {
				String sql_INSERT_NewMusicChannel = "INSERT INTO `Music_Channel`(`guild_ID`, `channel_ID`) "
												  + "VALUES (`" + channel.getGuild().getIdLong() + "`, `" + channel.getIdLong() + "`)";
				
				DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewMusicChannel);
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
	}
}
