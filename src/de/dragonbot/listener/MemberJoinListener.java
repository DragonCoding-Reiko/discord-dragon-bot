package de.dragonbot.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberJoinListener extends ListenerAdapter{

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Member joined = event.getMember();
		TextChannel channel;

		if((channel = event.getGuild().getDefaultChannel()) != null) {
			channel.sendMessage("Willkommen auf diesem Server, " + joined.getAsMention()).queue();
		}

	}

	public void onGuildMemberReemove(GuildMemberRemoveEvent event) {
		Member left = event.getMember();
		TextChannel channel;

		if((channel = event.getGuild().getDefaultChannel()) != null) {
			channel.sendMessage("Man sieht sich immer zwei Mal :) /n Bis zum nächsten Mal " + left.getAsMention()).queue();
		}

	}

}
