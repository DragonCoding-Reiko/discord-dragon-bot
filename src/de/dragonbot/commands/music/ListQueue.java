package de.dragonbot.commands.music;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ListQueue implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		getQueue(m);
	}

	public void getQueue(Member m) {

		GuildVoiceState state;
		if((state = m.getVoiceState()) != null) {
			VoiceChannel vc;
			if((vc = state.getChannel()) != null) {
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());

				Queue queue = controller.getQueue();
				List<AudioTrack> tracks = queue.getQueueList();

				EmbedBuilder builder = new EmbedBuilder();

				if(!tracks.isEmpty()) {
					int trackCount = tracks.size();
					builder.setDescription("**Queue:** \n \n" + trackCount + " Songs \n" + "**Next:** \n");



					int counter = 1;	
					for(AudioTrack track : tracks) {
						builder.addField(track.getInfo().author , "[" + track.getInfo().title + "](" + track.getInfo().uri + ")", false);

						if(counter > 10) {
							break;
						}
						counter++;
					}

				}else {
					builder.setDescription("**Queue:** \n" + "Keine Tracks in der Queue");
				}
				Utils.sendEmbed(builder, Utils.getMusicChannel(controller.getGuild().getIdLong()), 20l, null);
			}	
		}

	}

}
