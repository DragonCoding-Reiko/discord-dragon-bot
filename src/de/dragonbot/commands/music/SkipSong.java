package de.dragonbot.commands.music;

import java.awt.Color;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SkipSong implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		if(args.length > 1) {

			try {
				int skips = Integer.parseInt(args[1]);
				skip(channel, skips);
			} catch (NumberFormatException e){ 
				Utils.sendEmbed("ERROR", "Falsche Syntax! \n" + "Bitte nutze `#d skip` oder `#d skip <Zahl>`.", channel, 3l, new Color(0xff0000));
			}

		} else {
			skip(channel, 1);
		}

	}

	public void skip(TextChannel channel, int skips) {

		Guild guild = channel.getGuild();
		Long guildid = guild.getIdLong();
		MusicController controller = DragonBot.INSTANCE.playerManager.getController(guildid);
		Queue queue = controller.getQueue();

		if(!queue.getQueueList().isEmpty()) {
			boolean singleLoop = false;
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("**Skipped:**");
			builder.setDescription("Folgende Lieder wurden geskippt:");

			for(int i = 1; i <= (skips); i++) {
				AudioTrack next;
				if(i == 1) {
					next = queue.getLastList().get(0);
				} else {
					next = queue.getQueueList().get((i - 2));
				}
				singleLoop = queue.isSingleLoop();

				AudioTrackInfo info = next.getInfo();

				String title = info.title;
				String author = info.author;
				String url = info.uri;

				builder.addField(author, "[" + title + "](" + url + ")", false);
			}
			queue.skip(skips);

			if(!singleLoop) {
				Utils.sendEmbed(builder, channel, 15, null);
			}
		}
		else {
			Utils.sendEmbed("ERROR", "Keine Lieder in der Queue. -> Skippen nicht möglich!", channel, 3l, new Color(0xff0000));
		}
	}

}
