package de.dragonbot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter{


	public String[] prefixe = new String[] { "#d "};
	public String prefix;
	public int subString;
	public boolean isPrefix; //If message starts with one of the prefixes its true

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getChannelType() == ChannelType.TEXT) {
			Long channelid = dashboard(event.getGuild());
			
			if(channelid != event.getChannel().getIdLong()) {
				onMessage(event.getMessage(), event.getTextChannel(), event.getMember());
			}
			else if(event.getMessage().getAuthor() != event.getGuild().getSelfMember().getUser()) {
					
					event.getMessage().delete().queue();
			}
		}
	}

	public void onMessageUpdate(MessageUpdateEvent event) {
		if(event.getChannelType() == ChannelType.TEXT) {
			Long channelid = dashboard(event.getGuild());
			
			if(channelid != event.getChannel().getIdLong()) {
				onMessage(event.getMessage(), event.getTextChannel(), event.getMember());
			}
		}
	}

	public void onMessage(Message msg, TextChannel ch, Member m) {
		String message = msg.getContentDisplay();
		TextChannel channel = ch;

		isPrefix = false;
		for(String prefix : prefixe) {
			if(message.startsWith(prefix)) {
				this.prefix = prefix;
				this.subString = prefix.length();
				this.isPrefix = true;
				break;
			}
		}

		if(this.isPrefix) {
			String[] args = message.substring(this.subString).split(" ");

			if(args.length > 0) {
				if(!DragonBot.INSTANCE.getCmdMan().perform(args[0], m, channel, msg, this.subString)) {
					channel.sendMessage("`Unknown Command`").complete().delete().queueAfter(5, TimeUnit.SECONDS);
				}
			}	
		}
		else if(message.startsWith("#dragon")) {
			String command = msg.getContentDisplay().substring(1,7);
			this.subString = 1;
			if(!DragonBot.INSTANCE.getCmdMan().perform(command, m, channel, msg, this.subString)) {
				channel.sendMessage("`Unknown Command`").complete().delete().queueAfter(5, TimeUnit.SECONDS);
			}
		}
	}

	public Long dashboard(Guild guild) {
		ResultSet set = DragonBot.INSTANCE.listenerDB.getEntrys("channel_ID", "Dashboard", 
								"guild_ID = " + guild.getIdLong());
		
		long channelid = 0l;
		
		try {
			if(set.next()) {
				channelid = set.getLong("channel_ID");
			}
		} catch (SQLException e) { e.getMessage(); }
		
		return channelid;
	}
	
}


