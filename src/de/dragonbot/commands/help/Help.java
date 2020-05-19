package de.dragonbot.commands.help;

import java.awt.Color;

import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class Help implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();

		String[] args = message.getContentDisplay().substring(subString).split(" ");
		EmbedBuilder builder = new EmbedBuilder();

		builder.setDescription("**Bothilfe:** \n" 
				+ "`Prefix + Command` -> e.g. `#d help` \n \n");

		if(args.length == 1) {
			general(builder);
		} else {
			String help = args[1].toLowerCase();

			switch (help) {
			//Random
			case "invite":
				builder.addField("Invite", "Posts the Link to invite me to your server. \n See you soon :)", false);
				builder.addField("Usage", "`Prefix + invite` -> e.g. `#d invite` \n", false);
				break;
			case "ping":
				builder.addField("Ping", "Shows the ping the bot has. \n", false);
				builder.addField("Usage", "`Prefix + ping` -> e.g. `#d ping` \n", false);
				break;
			case "react":
				builder.addField("React", "Adds a reaction to a message. \n", false);
				builder.addField("Usage", "`Prefix + react <#channel> <MessageID> <Emotes>` \n -> e.g. `#d react #General 596676126995382282 🆗` \n", false);
				break;
			case "embed":
				builder.addField("Embed", "Sends your message, but more advanced. \n", false);
				builder.addField("Usage", "`Prefix + embed <message>` \n -> e.g. `#d embed This is a cool message.` \n", false);
				break;
				//Music
			case "play":
				builder.addField("Play", "Plays the song you choose. \n", false);
				builder.addField("Usage", "`Prefix + play <url/searchterm>` \n -> e.g. `#d play Song Artist` or `#d play <url>` \n", false);
				builder.addField("Note", "You can also add playlists :) \n", false);
				break;
			case "stop":
				builder.addField("Stop", "Stops the MusicBot. The bot leaves the VoiceChannel \n", false);
				builder.addField("Usage", "`Prefix + stop` \n -> e.g. `#d stop` \n", false);
				break;
			case "pause":
				builder.addField("Pause", "Pauses the MusicBot. \n", false);
				builder.addField("Usage", "`Prefix + pause` \n -> e.g. `#d pause` \n", false);
				break;
			case "skip":
				builder.addField("Skip", "Skips the current Song or the amount you told th Bot. \n", false);
				builder.addField("Usage", "`Prefix + skip [<number>]` \n -> e.g. `#d skip` or `#d skip 5", false);
				break;
			case "back":
				builder.addField("Back", "Plays the last played song. \n", false);
				builder.addField("Usage", "`Prefix + back` \n -> e.g. `#d back` \n", false);
				break;
			case "queue":
				builder.addField("queue", "Shows the next 10 songs in the queue. \n", false);
				builder.addField("Usage", "`Prefix + queue` \n -> e.g. `#d queue` \n", false);
				builder.addField("Note", "Will be upgraded soon! \n", false);
				break;
			case "loop":
				builder.addField("Loop", "Loops one song or the whole queue. \n", false);
				builder.addField("Usage", "`Prefix + loop [single]` \n -> e.g. `#d loop` or `#d loop single` \n", false);
				builder.addField("Note", "Loop: Loops over all songs. \n Loop Single: Loops over a single song. \n", false);
				break;
			case "np":
				builder.addField("NP", "Shows the current playing song. \n", false);
				builder.addField("Usage", "`Prefix + np` \n -> e.g. `#d np` \n", false);
				break;
			case "shuffle":
				builder.addField("Shuffle", "Shuffles the queue. A little bit mor random in everys life xD \n", false);
				builder.addField("Usage", "`Prefix + shuffle` \n -> e.g. `#d shuffle` \n", false);
				break;
				//Moderator
			case "clear":
				builder.addField("Clear", "Clears the mwntioned amount of messages from the channel its written in. \n", false);
				builder.addField("Usage", "`Prefix + clear <amount>` \n -> e.g. `#d clear 3` \n", false);
				break;
			case "reactrole":
				builder.addField("ReactRole", "Creates/Deletes a Reaction to/on a message which gives a role when clicked. \n", false);
				builder.addField("Usage - Create", "`Prefix + reactrole <#channel> <messageID> <:emote:> <@Rolle>` \n -> e.g. `#d reactrole #General 596676126995382282 🆗 @TestRole` \n", false);
				builder.addField("Usage - Delete Single", "`Prefix + reactrole remove <#channel> <messageID> <:emote:>` \n -> e.g. `#d reactrole remove #General 596676126995382282 🆗` \n", false);
				builder.addField("Usage - Delete All", "`Prefix + reactrole remove <#channel> <messageID> all` \n -> e.g. `#d reactrole remove #General 596676126995382282 all` \n", false);
				builder.addField("Note", "When clicking on a 'ReactRole-Reaction' you get the role. When removing your react you'll loose the role. \n", false);
				break;
			case "uinfo":
				builder.addField("UInfo", "Gives you informations about the user mentioned \n", false);
				builder.addField("Usage", "`Prefix + uinfo <@User>` \n -> e.g. `#d uinfo @dragonriderworld` \n", false);
				break;
			case "npchannel":
				builder.addField("NPChannel", "Creates a MusicDashboard from where you can controle the MusicBot by clicking on Reactions. Plus you get some informations bout the Music. \n", false);
				builder.addField("Usage", "`Prefix + npchannel` \n -> e.g. `#d npchannel` \n", false);
				builder.addField("Note", "**WIP** \n Is still in work, but will be finished soon := \n", false);
				break;
			case "stats":
				builder.addField("Stats", "Creates a category with 5 Channels that show some information: Time, Date, Server Members, Online Members and Bot State \n", false);
				builder.addField("Usage - Create", "`Prefix + stats` \n -> e.g. `#d stats` \n", false);
				builder.addField("Usage - Delete", "`Prefix + stats delete` \n -> e.g. `#d stats delete` \n", false);
				break;
			case "vchub":
				builder.addField("vchub", "Creates a VoiceChannel, that creates a seperate VoiceChannel for anybody joining it. This channel is only temporarily. Upon leaving that channel it will be deleted. \n", false);
				builder.addField("Usage - Create", "`Prefix + vchub create <name> [<max members>]` \n -> e.g. `#d vchub create TestHub1` or `#d vchub create TestHub2 3` \n", false);
				builder.addField("Usage - Delete", "`Prefix + vchub delete <name> ` \n -> e.g. `#d vchub delete TestHub1` \n", false);
				break;


			default:
				builder.addField("**Error**", "No Such Command: " + args[1], false);
				break;
			}
		}	
		Utils.sendEmbed(builder, channel, 30l, new Color(0xf42cbf4));

	}

	public EmbedBuilder general(EmbedBuilder builder) {

		builder.addField("More Information", "`Helpcommand + Command` -> e.g. `#d help invite` \n", false);

		builder.addField("__**Random**__", "`invite`, `ping`, `react`, `embed` \n", false);
		builder.addField("__**Music**__", "`play`, `stop`, `pause`, `skip`, `back`, `queue`, `loop`, `np`, `shuffle` \n", false);
		builder.addField("__**Moderator**__", "`clear`, `reactrole`, `uinfo`, `npchannel`, `stats`, `vchub` \n", false);

		builder.setFooter("Bot provided by dragonriderworld#3233");
		
		return builder;
	}

}
