package de.dragonbot.listener.dashboard;

import java.awt.Color;
import java.util.Arrays;

import de.dragonbot.DragonBot;
import de.dragonbot.listener.CommandListener;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DashboardTextListener extends ListenerAdapter{

	public String[] allowedCommands = new String[] { "back", "volume", "queue", "loop", "np", "pause", "playlist", "play", "shuffle", "skip", "stop"};
	public String prefix;
	public int subString;
	public boolean isPrefix; //If message starts with one of the prefixes its true
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if(event.getChannelType() == ChannelType.TEXT) {
			TextChannel channel = Utils.getDashboardChannel(event.getGuild());
			
			if(channel != null) {
				long channel_ID = channel.getIdLong();
				
				String message = event.getMessage().getContentDisplay();
	
				this.isPrefix = false;
				for(String prefix : CommandListener.prefixe) {
					if(message.startsWith(prefix)) {
						this.prefix = prefix;
						this.subString = this.prefix.length();
						this.isPrefix = true;
						
						break;
					}
				}
				
				if(this.isPrefix) {
					if(channel_ID == event.getChannel().getIdLong() && Arrays.asList(allowedCommands).contains(message.substring(this.subString).toLowerCase().split(" ")[0])) {
						onMessage(event.getMessage(), event.getTextChannel(), event.getMember());
					}
				}
				
				if(channel_ID == event.getChannel().getIdLong() && event.getMessage().getAuthor() != event.getGuild().getSelfMember().getUser()) {
					event.getMessage().delete().queue();
				}
			}
		}
	}

	
	public void onMessage(Message msg, TextChannel ch, Member m) {
		String message = msg.getContentDisplay();
		TextChannel channel = ch;
		
		if(this.isPrefix) {
			String[] args = message.substring(this.subString).split(" ");

			if(args.length > 0) {
				if(!DragonBot.INSTANCE.getCmdMan().perform(args[0], m, channel, msg, this.subString)) {
					Utils.sendEmbed("ERROR", "`Unknown Command`", channel, 3l, new Color(0xff0000));
				}
			}	
		}
		else if(message.startsWith("#dragon")) {
			String command = msg.getContentDisplay().substring(1,7);
			this.subString = 1;
			if(!DragonBot.INSTANCE.getCmdMan().perform(command, m, channel, msg, this.subString)) {
				Utils.sendEmbed("ERROR", "`Unknown Command`", channel, 3l, new Color(0xff0000));
			}
		}
	}
}
