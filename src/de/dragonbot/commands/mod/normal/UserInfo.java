package de.dragonbot.commands.mod.normal;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class UserInfo implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		List<Member> ment = message.getMentionedMembers();

		if(ment.size() > 0 ) {
			for(Member u : ment) {
				onInfo(m, u, channel);
			}
		}
	}

	public void onInfo(Member requester, Member u, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by " + requester.getUser().getName());
		builder.setColor(0xf42cbf4);
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());

		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("User: " + u.getAsMention() + "\n");
		strBuilder.append("ClientID: " + u.getId() + "\n");
		strBuilder.append("TimeJoined: " + formatDate(u.getTimeJoined()) + "\n");
		strBuilder.append("TimeCreated: " + formatDate(u.getTimeCreated()) + "\n");



		strBuilder.append("\n **Rollen:** \n");

		StringBuilder roleBuilder = new StringBuilder();
		for(Role role : u.getRoles()) {
			roleBuilder.append(role.getAsMention() + " ");
		}
		strBuilder.append(roleBuilder.toString().trim() + "\n");

		builder.setDescription(strBuilder);

		Utils.sendEmbed(builder, channel, 20, null);

	}

	public String formatDate(OffsetDateTime date) {

		DateTimeFormatter dte = DateTimeFormatter.ofPattern("dd.MM.YYYY HH:mm");

		String newDate = date.format(dte);
		return newDate;
	}

}
