package de.dragonbot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.dragonbot.manage.LiteSQL;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceListener extends ListenerAdapter{

	public List<Long> tempchannels;

	public VoiceListener() {
		this.tempchannels = new ArrayList<>();
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		onJoin(event.getChannelJoined(), event.getEntity());
	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
		onLeave(event.getChannelLeft());
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		onLeave(event.getChannelLeft());
		onJoin(event.getChannelJoined(), event.getEntity());
	}

	private void onJoin(VoiceChannel channelJoined, Member memb) {
		Boolean isHubVC = false;

		ResultSet set = LiteSQL.getEntrys("categoryid, channelid", 
				"voicechannelhubs", 
				"guildid = " + channelJoined.getGuild().getIdLong());

		try {
			while(set.next()) {
				Long category = set.getLong("categoryid");
				Long channel = set.getLong("channelid");

				if(channelJoined.getParent().getIdLong() == category) {
					if(channelJoined.getIdLong() == channel) {
						isHubVC = true;
						break;
					}
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if(isHubVC) {
			Category cat = channelJoined.getParent();
			int index = 1; 
			VoiceChannel vc = cat.createVoiceChannel( index + " | [" + memb.getEffectiveName().substring(0, 5) + "]").complete();
			vc.getManager().setUserLimit(channelJoined.getUserLimit()).queue();
			vc.getGuild().moveVoiceMember(memb, vc).queue();

			this.tempchannels.add(vc.getIdLong());
		}
	}

	private void onLeave(VoiceChannel channel) {
		if(channel.getMembers().size() <= 0) {
			if(this.tempchannels.contains(channel.getIdLong())) {
				this.tempchannels.remove(channel.getIdLong());
				channel.delete().queue();
			}
			int index = 1;
			for (Long long1 : tempchannels) {
				Category cat = channel.getParent();
				VoiceChannel vc = cat.getGuild().getVoiceChannelById(long1);
				String oldName = vc.getName();
				vc.getManager().setName(index + oldName.substring(oldName.lastIndexOf(" | "), 11)).queue();
				index++;
			}
		}
	}

}
