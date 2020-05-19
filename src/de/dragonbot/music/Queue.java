package de.dragonbot.music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Queue {

	private List<AudioTrack> queueList;
	private List<AudioTrack> lastSongs;
	
	private MusicController controller;
	private Guild guild;
	private boolean isFirst = true;
	private boolean isLoop;
	private boolean isSingleLoop;

	public Queue(MusicController controller) {
		
		this.controller = controller;
		this.setQueueList(new ArrayList<AudioTrack>());
		this.setLastList(new ArrayList<AudioTrack>());
		this.isLoop = false;
		this.isSingleLoop = false;
		this.guild = controller.getGuild();
	}

	public boolean isNext() {
		if(this.queueList.size() >= 1) {
			AudioTrack track = this.queueList.get(0);

			if(track != null) {
				return true;
			}
		}

		return false;
	}

	public boolean isLast() {
		if(this.lastSongs.size() >= 1) {
			AudioTrack track = this.lastSongs.get(0);

			if(track != null) {
				return true;
			}
		}

		return false;
	}

	public void addTrackToQueue0(AudioTrack track) {
		this.queueList.add(0, track.makeClone());
	}

	public void addTrackToQueueNew(AudioTrack track) {
		this.queueList.add(track);
		
		if(controller.getPlayer().getPlayingTrack() == null) {
			playNext();
		}
	}

	public void addTrackToQueue(AudioTrack track) {
		this.queueList.add(track.makeClone());
	}

	public void addTrackToLast0(AudioTrack track) {
		this.lastSongs.add(0, track.makeClone());
	}

	public void addTrackToLast(AudioTrack track) {
		this.lastSongs.add(track.makeClone());
	}

	public void playNext() {
		if(isNext()) {
			AudioTrack track = null;
			//No Loop active
			if(!this.isLoop && !this.isSingleLoop) {
				track = this.queueList.remove(0);
				addTrackToLast0(track);
			//Normal Loop active
			} else if(this.isLoop && !this.isSingleLoop) {
				track = this.queueList.remove(0);
				addTrackToLast0(track);
				addTrackToQueue(track);
			//Single Loop active
			} else if(this.isSingleLoop) {
				track = this.lastSongs.remove(0);
				addTrackToLast0(track);
			}
			this.controller.getPlayer().playTrack(track);
			
			TextChannel dashboard = Utils.getDashboardChannel(guild);
			if(dashboard != null) {
				MusicDashboard.updateQueue(dashboard, controller, controller.getPlayer());
			}
		}
	}

	public void playLast() {
		if(isLast()) {
			AudioTrack track = null;
			//No Loop active
			if(!this.isLoop && !this.isSingleLoop) {
				track = this.lastSongs.remove(0);
				addTrackToQueue0(track);
				//Normal Loop active
			} else if(this.isLoop && !this.isSingleLoop) {
				track = this.queueList.remove((queueList.size() - 1));
				addTrackToQueue0(track);
				this.lastSongs.remove(0);
				//Single Loop active
			} else if(this.isSingleLoop) {
				track = this.lastSongs.remove(0);
				addTrackToLast0(track);
			}
			this.controller.getPlayer().playTrack(track);

			TextChannel dashboard = Utils.getDashboardChannel(guild);
			if(dashboard != null) {
				MusicDashboard.updateQueue(dashboard, controller, controller.getPlayer());
			}
		}
	}

	public void skip(int times) {
		if(isNext()) {
			AudioTrack track = null;
			for(int i = 1; i <= times; i++) {
				track = this.queueList.remove(0);
				if(this.isLoop) {
					addTrackToQueue(track);
				}
				addTrackToLast0(track);
				if(i == times) {
					this.controller.getPlayer().playTrack(track);
				}
			}

			TextChannel dashboard = Utils.getDashboardChannel(guild);
			if(dashboard != null) {
				MusicDashboard.updateQueue(dashboard, controller, controller.getPlayer());
			}
		}
	}

	public void shuffle() {
		Collections.shuffle(this.queueList);
		Collections.shuffle(this.lastSongs);
	}

	public void loop(TextChannel channel) {
		if(this.isLoop) {
			this.isLoop = false;
		} else {
			this.isLoop = true;
		}
		if(this.isSingleLoop) {
			Utils.sendEmbed("INFO", "Der Befehl hat keine Wirkung, da ein Single Loop aktiviert ist. \n" + "Benutze `#d loop single` um den Single Loop zu deaktivieren.", channel, 10l, null);
		}
	}

	public void singleLoop() {
		if(this.isSingleLoop) {
			this.isSingleLoop = false;
		} else {
			this.isSingleLoop = true;
		}
	}

	public int getSongIndexQueue(String searchterm) {
		int index = 0;
		String search = "*" + searchterm.toLowerCase().replaceAll(" ", "*") + "*";

		for(AudioTrack track : this.queueList) {
			String title = track.getInfo().title.toLowerCase();
			if(title.contains(search)) {
				index = this.queueList.indexOf(track);
				break;
			}
		}

		return index;
	}
	
	public void removeCurrentFromQueue(AudioTrack removeTrack) {
		int index = 0;
		String title = removeTrack.getInfo().title;

		for(AudioTrack track : this.queueList) {
			String queueTitle = track.getInfo().title;
			if(queueTitle.equals(title)) {
				index = this.queueList.indexOf(track);
				break;
			}
		}
		this.queueList.remove(index);
		
	}
	

	public void deleteTrackFromQueue(int index) {
		this.queueList.remove(index);
	}

	public void onStop() {
		if(this.queueList != null) this.queueList.clear();
		if(this.lastSongs != null) this.lastSongs.clear();
		this.isLoop = false;
		this.isSingleLoop = false;
	}
	
	public void onPlaylist() {
		if(this.queueList != null) this.queueList.clear();
		if(this.lastSongs != null) this.lastSongs.clear();
	}

	//Controller Getter and Setter
	public MusicController getController() {
		return this.controller;
	}

	public void setController(MusicController controller) {
		this.controller = controller;
	}


	//queuelist Getter and Setter
	public List<AudioTrack> getQueueList() {
		return queueList;
	}

	public void setQueueList(List<AudioTrack> queueList) {
		this.queueList = queueList;
	}


	//lastList Getter and Setter
	public List<AudioTrack> getLastList() {
		return this.lastSongs;
	}

	public void setLastList(List<AudioTrack> lastSongs) {
		this.lastSongs = lastSongs;
	}


	//Bool Getter
	public boolean isSingleLoop() {
		return this.isSingleLoop;
	}
	
	public boolean isLoop() {
		return this.isLoop;
	}

	public boolean isFirst() {
		return this.isFirst;
	}
	
	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
}
