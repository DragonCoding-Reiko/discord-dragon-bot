package de.dragonbot.music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;

public class AudioLoadResult implements AudioLoadResultHandler{

	private final MusicController controller;

	public AudioLoadResult(MusicController controller) {
		this.controller = controller;
	}

	@Override
	public void loadFailed(FriendlyException arg0) {
		arg0.printStackTrace();
	}

	@Override
	public void noMatches() {
		System.out.println("No matches");
	}

	@Override
	public void playlistLoaded(AudioPlaylist arg0) {
		Queue queue = controller.getQueue();


		if(arg0.isSearchResult()) {
			queue.addTrackToQueueNew(arg0.getTracks().get(0));

			EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#8c14fc"))
					.setDescription("Titel zu Queue hinzugefügt: \n [" 
							+ arg0.getTracks().get(0).getInfo().title + "](" + arg0.getTracks().get(0).getInfo().uri + ")");

			MusicUtil.sendEmbed(controller.getGuild().getIdLong(), builder);
			return;
		}

		int added = 0;
		for(AudioTrack track : arg0.getTracks()) {
			queue.addTrackToQueueNew(track);
			added++;
		}

		EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#8c14fc"))
				.setDescription(added + " Titel zu Queue hinzugefügt.");

		MusicUtil.sendEmbed(controller.getGuild().getIdLong(), builder);
	}

	@Override
	public void trackLoaded(AudioTrack arg0) {
		Queue queue = controller.getQueue();
		queue.addTrackToQueueNew(arg0);

		EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#8c14fc"))
				.setDescription("Titel zu Queue hinzugefügt: \n [" 
						+ arg0.getInfo().title + "](" + arg0.getInfo().uri + ")");

		MusicUtil.sendEmbed(controller.getGuild().getIdLong(), builder);
	}

}
