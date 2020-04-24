package de.dragonbot.listener.member;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberLeaveListener extends ListenerAdapter{

	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		Member left = event.getMember();
		TextChannel channel;

		if((channel = event.getGuild().getDefaultChannel()) != null) {
			channel.sendMessage("Man sieht sich immer zwei Mal :) /n Bis zum nächsten Mal " + left.getAsMention()).queue();
		}

	}
	
}
