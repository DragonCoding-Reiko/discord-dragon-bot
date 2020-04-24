package de.dragonbot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.dragonbot.commands.mod.channel.Stats;
import de.dragonbot.listener.CommandListener;
import de.dragonbot.listener.VoiceListener;
import de.dragonbot.listener.dashboard.DashboardReactionListener;
import de.dragonbot.listener.dashboard.DashboardTextListener;
import de.dragonbot.listener.guild.GuildJoinListener;
import de.dragonbot.listener.guild.GuildLeaveListener;
import de.dragonbot.listener.member.MemberJoinListener;
import de.dragonbot.listener.member.MemberLeaveListener;
import de.dragonbot.listener.reactrole.ReactRoleAddListener;
import de.dragonbot.listener.reactrole.ReactRoleRemoveListener;
import de.dragonbot.manage.CommandManager;
import de.dragonbot.manage.LiteSQL;
import de.dragonbot.manage.SQLManager;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.MusicDashboard;
import de.dragonbot.music.PlayerManager;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DragonBot {

	public static DragonBot INSTANCE;

	public String token;
	public String link;
	public String mysqlLink;
	public String mysqlUser;
	public String mysqlPswd;
	
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

JSONParser jsonParser = new JSONParser();
		
		try (FileReader reader = new FileReader("DONOTOPEN.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            JSONArray infoList = (JSONArray) obj;

            JSONObject jsonObj = (JSONObject) infoList.get(0);
            
            this.token = jsonObj.get("token").toString();
            this.link = jsonObj.get("link").toString();

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
		
		LiteSQL.connect();
		SQLManager.onCreate();
		
		@SuppressWarnings("deprecation")
		DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder();
		builder.setToken(this.token);
		builder.setStatus(OnlineStatus.ONLINE);

		this.audioPlayermanager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();

		this.cmdMan = new CommandManager();

		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		
		builder.addEventListeners(new ReactRoleAddListener());
		builder.addEventListeners(new ReactRoleRemoveListener());
		
		builder.addEventListeners(new MemberJoinListener());
		builder.addEventListeners(new MemberLeaveListener());
		
		builder.addEventListeners(new GuildJoinListener());
		builder.addEventListeners(new GuildLeaveListener());
		
		builder.addEventListeners(new DashboardReactionListener());
		builder.addEventListeners(new DashboardTextListener());

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
					shutdownMessage = line.substring((line.indexOf(" ") + 1));

					if(line.toLowerCase().startsWith("exit")) {

						for(Guild guild : shardMan.getGuilds()) {
							System.out.println(guild.getName());
							
							if((server = guild.getDefaultChannel()) != null) {
								server.sendMessage("Der Bot fährt jetzt herunter. \n" + "Grund: " +  shutdownMessage).queue();
							}
						}
						System.out.println(" ");
						
						shutdown = true;
						if(shardMan != null) {
							for(Guild guild : shardMan.getGuilds()) {
								MusicController controller = DragonBot.INSTANCE.playerManager.getController(guild.getIdLong());
								Queue queue = controller.getQueue();
								AudioPlayer player = controller.getPlayer();
								AudioManager manager = guild.getAudioManager();
								queue.onStop();
								queue.setFirst(true);
								player.stopTrack();
								manager.closeAudioConnection();
							}
							Stats.onShutdown();
							MusicDashboard.onShutdown();
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
							System.out.println(guild.getName());
							if((server = guild.getDefaultChannel()) != null) {
								server.sendMessage("Der Bot restartet. \n" + "Grund: " +  shutdownMessage).queue();
							}
						}

						shutdown = true;
						if(shardMan != null) {
							Stats.onShutdown();
							MusicDashboard.onShutdown();
							shardMan.setStatus(OnlineStatus.OFFLINE);
							shardMan.shutdown();
							LiteSQL.disconnect();
							
							System.out.println("Bot is offline!");
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

	String[] status = new String[] {"auf %server Servern", "#dragon"};
	int next = 0;
	int playingInfo = 0;
	int queueInfo = 0;
	int stats = 0;

	public void onSecond() {

		if(playingInfo == 0) {
			MusicDashboard.update();
			
			playingInfo = 2;
		} 
		else {
			playingInfo--;
		}
		
		if(stats == 0) {
			Stats.checkStats();
			
			stats = 5;
		} 
		else {
			stats--;
		}
		
		if(next == 0) {
			if(!hasStarted) {
				hasStarted = true;
				Stats.onStartUP();
				MusicDashboard.onStartUp();
			}

			Random rand = new Random();		
			int i = rand.nextInt(status.length);

			shardMan.getShards().forEach(jda -> {
				String text = status[i].replaceAll("%server", "" + jda.getGuilds().size());

				jda.getPresence().setActivity(Activity.listening(text));
			});

			next = 5;
		}
		else {
			next--;
		}

	}

	public CommandManager getCmdMan() {
		return cmdMan;
	}
	public ShardManager getShardMan() {
		return shardMan;
	}

}
