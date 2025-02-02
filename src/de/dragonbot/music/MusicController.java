package de.dragonbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.dragonbot.DragonBot;
import net.dv8tion.jda.api.entities.Guild;

public class MusicController {

	private Guild guild;
	private AudioPlayer player;
	private Queue queue;
	private boolean loadedFromInternalPlaylist;

	public MusicController(Guild guild) {
		this.guild = guild;
		this.player = DragonBot.INSTANCE.audioPlayermanager.createPlayer();
		this.queue = new Queue(this);

		this.guild.getAudioManager().setSendingHandler(new AudioPlayerSentHandler(player));

		this.player.addListener(new TrackScheduler());
		this.player.setVolume(10);
		this.loadedFromInternalPlaylist = false;
	}
	
	public void SetLoadedFromInternalPlaylist(boolean isLFIP) {
		loadedFromInternalPlaylist = isLFIP;
	}
	
	public Guild getGuild() {
		return guild;
	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public Queue getQueue() {
		return queue;
	}
	
	public boolean isLFIP() {
		return loadedFromInternalPlaylist;
	}

}