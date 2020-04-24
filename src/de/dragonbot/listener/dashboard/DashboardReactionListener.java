package de.dragonbot.listener.dashboard;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.music.ChangeVolume;
import de.dragonbot.manage.MySQL;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.MusicDashboard;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class DashboardReactionListener extends ListenerAdapter{

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		if(!event.getUser().isBot()) {
			ResultSet set = MySQL.getEntrys("channel_ID", "Dashboard", "guild_ID = " + event.getGuild().getIdLong());

			try {
				if(set.next()) {
					if(event.getTextChannel().getIdLong() == set.getLong("channel_ID")) {
						String emote = event.getReactionEmote().getEmoji();
						String[] emotes = new String[] { "🔉", "🔊", "🔄", "🔀", "⏹️", "⏮️", "⏸️", "⏭️", "🔁", "🔂", "❌", "❓"};

						int index = 1;
						for(String str : emotes) {
							if(str.equals(emote)) {
								Member memb = event.getMember();
								callAction(index, event.getGuild(), event.getTextChannel(), memb);

								event.getReaction().removeReaction(memb.getUser()).queue();
								break;
							}

							index++;
						}
					}
					return;
				}
			} catch (SQLException e) { e.printStackTrace(); }
		}
	}

	public void callAction(int index, Guild guild, TextChannel channel, Member member) {

		MusicController controller = DragonBot.INSTANCE.playerManager.getController(guild.getIdLong());
		AudioPlayer player = controller.getPlayer();
		Queue queue = controller.getQueue();
		AudioManager manager = guild.getAudioManager();

		switch (index) {
		case 1:
			ChangeVolume.setVolume(player.getVolume() - 5, player);
			MusicDashboard.updateVolume(channel, player);
			break;
		case 2:
			ChangeVolume.setVolume(player.getVolume() + 5, player);
			MusicDashboard.updateVolume(channel, player);
			break;
		case 3:
			ChangeVolume.setVolume(10, player);
			MusicDashboard.updateVolume(channel, player);
			break;
		case 4:
			queue.shuffle();
			break;
		case 5:
			controller.getQueue().onStop();

			player.stopTrack();
			player.setPaused(false);

			manager.closeAudioConnection();		
			break;
		case 6:
			queue.playLast();
			break;
		case 7:
			if(player.isPaused()) {
				player.setPaused(false);
			} else {
				player.setPaused(true);
			}
			break;
		case 8:
			queue.skip(1);
			break;
		case 9:
			queue.loop(channel);
			break;
		case 10:
			queue.singleLoop();
			break;
		case 11:
			queue.deleteTrackFromQueue(0);
			break;
		case 12:
			help(member);
		
		default:
			break;
		}
	}
	
	public void help(Member member) {
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle("**Dashboard-Help: **");
		builder.setFooter("Provided by dragonriderworld");
		
		builder.addField(":x:", "Deletes the next track.", true);
		builder.addField("❓", "Help comes.", true);
		
		builder.addField("", "", false);
		
		builder.addField("🔀", "Shufflest the songs.", true);
		builder.addField("⏹️", "Stops the Music bot.", true);
		builder.addField("⏮️", "Goes one song back.", true);
		builder.addField("⏸️", "Pauses/Resumes the song.", true);
		builder.addField("⏭️", "Next Song.", true);
		builder.addField("🔁", "Loops over all songs.", true);
		builder.addField("🔂", "Loops a single song.", true);
		
		builder.addField("", "", false);
		
		builder.addField("🔉", "Please...be quieter.", true);
		builder.addField("🔊", "Louder... mix it up.", true);
		builder.addField("🔄", "Resets the volume to 10.", true);
		
		PrivateChannel channel = member.getUser().openPrivateChannel().complete();
		channel.sendMessage(builder.build()).queue();
	}
}






