package de.dragonbot.listener.member;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
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

}
