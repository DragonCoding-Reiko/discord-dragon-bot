package de.dragonbot.manage;

import java.util.concurrent.ConcurrentHashMap;

import de.dragonbot.commands.ServerCommand;
import de.dragonbot.commands.aikakone.GetPlayerScores;
import de.dragonbot.commands.aikakone.GetTop3;
import de.dragonbot.commands.help.Help;
import de.dragonbot.commands.mod.channel.NPChannel;
import de.dragonbot.commands.mod.channel.Stats;
import de.dragonbot.commands.mod.channel.VCHub;
import de.dragonbot.commands.mod.normal.Clear;
import de.dragonbot.commands.mod.normal.CreateReactRole;
import de.dragonbot.commands.mod.normal.UserInfo;
import de.dragonbot.commands.music.BackSong;
import de.dragonbot.commands.music.ChangeVolume;
import de.dragonbot.commands.music.ListQueue;
import de.dragonbot.commands.music.LoopSong;
import de.dragonbot.commands.music.NowPlaying;
import de.dragonbot.commands.music.PauseSong;
import de.dragonbot.commands.music.PlaySong;
import de.dragonbot.commands.music.ShuffleOueue;
import de.dragonbot.commands.music.SkipSong;
import de.dragonbot.commands.music.StopSong;
import de.dragonbot.commands.random.Invite;
import de.dragonbot.commands.random.Ping;
import de.dragonbot.commands.random.Reaction;
import de.dragonbot.commands.random.SendAsEmbed;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandManager {

	public ConcurrentHashMap<String, ServerCommand> commands;

	public CommandManager() {
		this.commands = new ConcurrentHashMap<>();
		//Commands

		//HelpCommands
		this.commands.put("dragon", new Help());
		this.commands.put("help", new Help());

		//RamdomCommands - No Category
		this.commands.put("invite", new Invite());
		this.commands.put("ping", new Ping());
		this.commands.put("react", new Reaction());
		this.commands.put("embed", new SendAsEmbed());

		//ModeratorCommands - Advanced Server Preferences
		this.commands.put("clear", new Clear());
		this.commands.put("reactrole", new CreateReactRole());
		this.commands.put("uinfo", new UserInfo());

		//ModeratorCommands - Create Server Rooms
		this.commands.put("npchannel", new NPChannel());
		this.commands.put("stats", new Stats());
		this.commands.put("vchub", new VCHub());

		//MusicCommands - Manage your Music
		this.commands.put("volume", new ChangeVolume());
		this.commands.put("back", new BackSong());
		this.commands.put("queue", new ListQueue());
		this.commands.put("loop", new LoopSong());
		this.commands.put("np", new NowPlaying());
		this.commands.put("pause", new PauseSong());
		this.commands.put("play", new PlaySong());
		this.commands.put("shuffle", new ShuffleOueue());
		this.commands.put("skip", new SkipSong());
		this.commands.put("stop", new StopSong());

		//BDOCommands - Informations and Utilities for BDO

		
		//Aikakone - Utility Commands for the Unity Game
		this.commands.put("top3", new GetTop3());
		this.commands.put("ps", new GetPlayerScores());

	}

	public boolean perform(String command, Member m, TextChannel channel, Message message, int subString) {

		ServerCommand cmd;
		if((cmd = this.commands.get(command.toLowerCase())) != null) {
			cmd.performCommand(m, channel, message, subString);
			return true;
		}

		return false;
	}
}
