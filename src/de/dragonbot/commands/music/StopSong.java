package de.dragonbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.MusicController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopSong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		GuildVoiceState state;
		if((state = m.getVoiceState()) != null) {
			VoiceChannel vc;
			if((vc = state.getChannel()) != null) {
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
				AudioManager manager = vc.getGuild().getAudioManager();
				AudioPlayer player = controller.getPlayer();

				Utils.setMusicChannel(channel);

				controller.getQueue().onStop();

				player.stopTrack();
				player.setPaused(false);
				manager.closeAudioConnection();			
			}
		}
	}

}
