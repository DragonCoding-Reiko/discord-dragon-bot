package de.dragonbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.dragonbot.commands.mod.channel.Stats;
import de.dragonbot.listener.CommandListener;
import de.dragonbot.listener.MemberJoinListener;
import de.dragonbot.listener.ReactionListener;
import de.dragonbot.listener.VoiceListener;
import de.dragonbot.manage.CommandManager;
import de.dragonbot.manage.DONOTOPEN;
import de.dragonbot.manage.LiteSQL;
import de.dragonbot.manage.SQLManager;
import de.dragonbot.music.PlayerManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DragonBot {

	public static DragonBot INSTANCE;

	public ShardManager shardMan;
	private CommandManager cmdMan;
	private Thread loop;
	public AudioPlayerManager audioPlayermanager;
	public PlayerManager playerManager;

	public static void main(String[] args) throws LoginException, IllegalArgumentException {
		try {
			new DragonBot();
		}
		catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public DragonBot() throws LoginException, IllegalArgumentException {
		INSTANCE = this;

		LiteSQL.connect();
		SQLManager.onCreate();


		@SuppressWarnings("deprecation")
		DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
		builder.setToken(DONOTOPEN.token);
		builder.setStatus(OnlineStatus.ONLINE);

		this.audioPlayermanager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();

		this.cmdMan = new CommandManager();

		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		builder.addEventListeners(new ReactionListener());
		builder.addEventListeners(new MemberJoinListener());

		shardMan = builder.build();
		System.out.println("Status: Online.");

		AudioSourceManagers.registerRemoteSources(audioPlayermanager);
		audioPlayermanager.getConfiguration().setFilterHotSwapEnabled(true);

		shutdown();
		runLoop();	
	}

	public void shutdown() {

		new Thread(() -> {

			String line = "";
			String shutdownMessage = "";
			TextChannel server;
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			try {
				while((line = reader.readLine()) != null) {
					System.out.println();
					System.out.println(line.toLowerCase());
					shutdownMessage = line.substring((line.indexOf(" ") + 1));

					if(line.toLowerCase().startsWith("exit")) {

						for(Guild guild : shardMan.getGuilds()) {
							System.out.println(guild.getName() + " " + guild.getIdLong());
							if((server = guild.getDefaultChannel()) != null) {
								server.sendMessage("Der Bot fährt jetzt herunter. \n" + "Grund: " +  shutdownMessage).queue();
							}
						}

						shutdown = true;
						if(shardMan != null) {
							Stats.onShutdown();
							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();
							LiteSQL.disconnect();

							System.out.println("Bot is offline.");
						}
						if(loop != null) {
							loop.interrupt();
						}
						reader.close();
						break;
					}
					else if(line.toLowerCase().startsWith("info")) {
						for(Guild guild : shardMan.getGuilds()) {
							System.out.println(guild.getName() + " " + guild.getIdLong());
						}
					}
					else if(line.toLowerCase().startsWith("restart")) {

						for(Guild guild : shardMan.getGuilds()) {
							System.out.println(guild.getName() + " " + guild.getIdLong());
							if((server = guild.getDefaultChannel()) != null) {
								server.sendMessage("Der Bot restartet. \n" + "Grund: " +  shutdownMessage).queue();
							}
						}

						shutdown = true;
						if(shardMan != null) {
							Stats.onShutdown();
							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();
							LiteSQL.disconnect();

							System.out.println("Bot is offline.");
						}
						if(loop != null) {
							loop.interrupt();
						}
						try {
							new DragonBot();
						}
						catch (LoginException | IllegalArgumentException e) {
							e.printStackTrace();
						}
						break;
					}
					else {
						System.out.println("Use 'Exit' to shutdown.");
					}
				}
			}catch (IOException e){

			}


		}).start();
	}

	public boolean shutdown = false;
	public boolean hasStarted = false;

	public void runLoop() {
		this.loop = new Thread(() -> {

			long time = System.currentTimeMillis();

			while(!shutdown) {
				if(System.currentTimeMillis() >= time + 1000) {
					time = System.currentTimeMillis();
					onSecond();
				}
			}


		});
		this.loop.setName("Loop");
		this.loop.start();
	}

	String[] status = new String[] {"auf %server Servern", "Dev's Freunden", "seinem Dev", "deinen Befehlen", "#dragon", "#d help"};
	int[] colors = new int[] {0xff9478, 0xd2527f, 0x00b5cc, 0x19b5fe, 0x2ecc71, 0x23cba7, 0x00e640, 0x8c14fc, 0x9f5afd, 0x663399};
	int next = 0;

	public void onSecond() {

		if(next == 0 || next == 5) {
			if(!hasStarted) {
				hasStarted = true;
				Stats.onStartUP();
			}

			Random rand = new Random();		
			int i = rand.nextInt(status.length);

			shardMan.getShards().forEach(jda -> {
				String text = status[i].replaceAll("%server", "" + jda.getGuilds().size());

				jda.getPresence().setActivity(Activity.listening(text));
			});

			Stats.checkStats();

			next = 10;
		}
		else {
			next--;
		}

		//MusicDashboard.refresh
	}

	public CommandManager getCmdMan() {
		return cmdMan;
	}
	public ShardManager getShardMan() {
		return shardMan;
	}

}
