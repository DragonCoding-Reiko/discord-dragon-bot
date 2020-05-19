package de.dragonbot.music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.entities.TextChannel;

public class AudioLoadResult implements AudioLoadResultHandler{

	private final MusicController controller;
	private boolean isFromInternalPlaylist;

	public AudioLoadResult(MusicController controller) {
		this.controller = controller;
		this.isFromInternalPlaylist = controller.isLFIP();
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
			System.out.println("Identifiere: " + arg0.getTracks().get(0).getIdentifier());
			System.out.println("Title: " + arg0.getTracks().get(0).getInfo().title);
			
			if((queue.getQueueList().size() + 1) <= 500) {
				queue.addTrackToQueueNew(arg0.getTracks().get(0));
				if(!isFromInternalPlaylist) {
					String message = "Titel zu Queue hinzugefügt: \n [" + arg0.getTracks().get(0).getInfo().title + "](" + arg0.getTracks().get(0).getInfo().uri + ")";
					
					Utils.sendEmbed("INFO", message, Utils.getMusicChannel(controller.getGuild().getIdLong()), 5l, null);
				}
				return;
			} else {
				if(!isFromInternalPlaylist) {
					String message = "Titel nicht zu Queue Hinzugefügt: \n " + "Maximale Queue Size 500";
					
					Utils.sendEmbed("ERROR", message, Utils.getMusicChannel(controller.getGuild().getIdLong()), 3l, new Color(0xff00000));
				}
				return;
			}
		}

		int added = 0;
		int notAdded = 0;
		for(AudioTrack track : arg0.getTracks()) {
			if((queue.getQueueList().size() + 1) <= 500) {
				
				System.out.println("Identifiere: " + track.getIdentifier());
				System.out.println("Title: " + track.getInfo().title);
				
				queue.addTrackToQueueNew(track);
				added++;
			} else notAdded++;
		}
		
		TextChannel dashboard = Utils.getDashboardChannel(controller.getGuild());
		if(dashboard != null) {
			MusicDashboard.updateQueue(dashboard, controller, controller.getPlayer());
		}
		
		if(!isFromInternalPlaylist) {
			String message = added + " Titel zu Queue hinzugefügt. \n " + notAdded + " Titel übersprungen -> Maximale Queue Size 500";
			
			Utils.sendEmbed("INFO", message, Utils.getMusicChannel(controller.getGuild().getIdLong()), 5l, null);
		}
	}

	@Override
	public void trackLoaded(AudioTrack arg0) {
		System.out.println("Identifiere: " + arg0.getIdentifier());
		System.out.println("Title: " + arg0.getInfo().title);
		
		Queue queue = controller.getQueue();
		
		if((queue.getQueueList().size() + 1) <= 500) {
			
			queue.addTrackToQueueNew(arg0);
			if(!isFromInternalPlaylist) {
				String message = "Titel zu Queue hinzugefügt: \n [" + arg0.getInfo().title + "](" + arg0.getInfo().uri + ")";
				
				Utils.sendEmbed("INFO", message, Utils.getMusicChannel(controller.getGuild().getIdLong()), 5l, null);
			}
			
			TextChannel dashboard = Utils.getDashboardChannel(controller.getGuild());
			if(dashboard != null) {
				MusicDashboard.updateQueue(dashboard, controller, controller.getPlayer());
			}
			return;
		} else {
			if(!isFromInternalPlaylist) {
				String message = "Titel nicht zu Queue Hinzugefügt: \n " + "Maximale Queue Size 500";
				
				Utils.sendEmbed("ERROR", message, Utils.getMusicChannel(controller.getGuild().getIdLong()), 3l, new Color(0xff00000));
			}
			return;
		}

	}
}
