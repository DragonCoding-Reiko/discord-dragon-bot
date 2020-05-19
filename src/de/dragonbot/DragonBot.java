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
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import de.dragonbot.commands.mod.channel.Stats;
import de.dragonbot.listener.CommandListener;
import de.dragonbot.listener.VoiceListener;
import de.dragonbot.listener.dashboard.DashboardReactionListener;
import de.dragonbot.listener.dashboard.DashboardTextListener;
import de.dragonbot.listener.guild.GuildJoinListener;
import de.dragonbot.listener.guild.GuildLeaveListener;
import de.dragonbot.listener.guild.GuildNameListener;
import de.dragonbot.listener.member.MemberJoinListener;
import de.dragonbot.listener.member.MemberLeaveListener;
import de.dragonbot.listener.reactrole.ReactRoleAddListener;
import de.dragonbot.listener.reactrole.ReactRoleRemoveListener;
import de.dragonbot.manage.CommandManager;
import de.dragonbot.manage.MySQL;
import de.dragonbot.manage.SQLManager;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.MusicDashboard;
import de.dragonbot.music.PlayerManager;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class DragonBot {

	public static DragonBot INSTANCE;
	
	public MySQL mainDB;
	public MySQL shutdownDB;
	public MySQL loopDB;
	public MySQL listenerDB;
	
	public MySQL gameDB;
	
	public String token; //Discord Application Token
	public String link; //Invite Link for the bot
	public String mysqlLink; //Link to the Bot MySQL Database
	public String mysqlUser; //User for the Databases
	public String mysqlPswd; //Password for the databases
	public String botDBName; //Databasename of the Bot's DB
	public String gameDBName; //Databasename of the Games's DB
	
	public ShardManager shardMan;
	private CommandManager cmdMan;
	private Thread loop;
	public YoutubeAudioSourceManager yt;
	public AudioPlayerManager audioPlayermanager;
	public PlayerManager playerManager;

	public static void main(String[] args){
		try {
			new DragonBot();
		}
		catch (LoginException | IllegalArgumentException e) {
			Utils.printError(e, null);
		}
	}

	public DragonBot() throws LoginException, IllegalArgumentException {
		INSTANCE = this;
		this.mainDB = new MySQL();
		this.loopDB = new MySQL();
		this.listenerDB = new MySQL();
		this.shutdownDB = new MySQL();
		
		this.gameDB = new MySQL();
		
		JSONParser jsonParser = new JSONParser();
		
		try (FileReader reader = new FileReader("DONOTOPEN.json"))
		{
		    //Read JSON file
			    Object obj = jsonParser.parse(reader);
 
			    JSONArray infoList = (JSONArray) obj;

			    JSONObject jsonObj = (JSONObject) infoList.get(0);
			    
			    this.token = jsonObj.get("token").toString();
		    this.link = jsonObj.get("link").toString();
		    this.mysqlLink = jsonObj.get("mysqlLink").toString();
		    this.mysqlUser = jsonObj.get("mysqlUser").toString();
		    this.mysqlPswd = jsonObj.get("mysqlPswd").toString();
		    this.botDBName = jsonObj.get("botDB").toString();
		    this.gameDBName = jsonObj.get("gameDB").toString();
		    
		} catch (ParseException | IOException e) {
			Utils.printError(e, null);
		}
		
		this.mainDB.connect(this.botDBName);
		this.loopDB.connect(this.botDBName);
		this.listenerDB.connect(this.botDBName);
		this.shutdownDB.connect(this.botDBName);
		
		this.gameDB.connect(this.gameDBName);
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
		builder.addEventListeners(new GuildNameListener());

		shardMan = builder.build();
		System.out.println("Status: Online.");
		
		yt = new YoutubeAudioSourceManager();
		yt.setPlaylistPageCount(6);
		
		audioPlayermanager.registerSourceManager(yt);
		
		AudioSourceManagers.registerRemoteSources(audioPlayermanager);
		audioPlayermanager.getConfiguration().setFilterHotSwapEnabled(true);
		
		shutdown();
		runLoop();
	}

	public void shutdown() {
		new Thread(() -> {

			String line = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			try {
				while((line = reader.readLine().toLowerCase()) != null) {
					String cmd = line.split(" ")[0];
					String shutdownMessage = line.substring((line.indexOf(" ") + 1));
					System.out.println(" ");
					
					switch (cmd) {
						case "exit":
							
							exit();
							reader.close();
							break;
							
						case "guilds":
							
							guilds();
							break;
	
						case "restart":
							
							restart();
							break;
							
						case "members":
							
							members(Integer.parseInt(shutdownMessage.split(" ")[0]));
							break;
							
						default:
							
							System.out.println("Unknown Command.");
							break;
					}
					
				}
			}catch (IOException e){ }


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
					
					try {
						Thread.sleep(750);
					} catch (InterruptedException e) { }
				}
			}
		});
		this.loop.setName("Update Thread");
		this.loop.start();
	}

	String[] status = new String[] {"auf %server Servern", "#dragon"};
	int all_10_Sec = 1;
	int all_5_Sec = 1;
	int all_2_Sec = 1;

	public void onSecond() {

		if(all_2_Sec == 0 && !shutdown) {
			MusicDashboard.update();
			
			all_2_Sec = 2;
		} 
		else all_2_Sec--;
		
		
		if(all_5_Sec == 0 && !shutdown) {
			Stats.checkStats();
			all_5_Sec = 5;
		} 
		else all_5_Sec--;
		
		
		if(all_10_Sec == 0 && !shutdown) {
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

			all_10_Sec = 10;
		}
		else all_10_Sec--;
	}

	public CommandManager getCmdMan() {
		return cmdMan;
	}
	public ShardManager getShardMan() {
		return shardMan;
	}
	
	private void exit() {
		onShutdown();
	}
	
	private void guilds() {
		System.out.println("");
		System.out.println("========================================");
		System.out.println("Server, auf denen der Bot ist (" + shardMan.getGuilds().size() + "):");
		System.out.println("========================================");
		
		int i = 0;
		for(Guild guild : shardMan.getGuilds()) {
			System.out.println("----------------------------------------");
			System.out.println("Index: " + i);
			System.out.println("Name: " + guild.getName());
			System.out.println("ID: " + guild.getIdLong());
			System.out.println("----------------------------------------");
			
			i++;
		}
		
		System.out.println("========================================");
	}
	
	private void restart() {
		onShutdown();
		try {
			new DragonBot();
		}
		catch (LoginException | IllegalArgumentException e) {
			Utils.printError(e, null);
		}
	}
	
	private void members(int index) {
		if(index < shardMan.getGuilds().size()) {
			Guild guild = shardMan.getGuilds().get(index);
			
			System.out.println("");
			System.out.println("========================================");
			System.out.println("Member auf dem Server " + guild.getName() + "(" + guild.getMembers().size() + "):");
			System.out.println("========================================");
			
			for(Member memb : guild.getMembers()) {
				System.out.println("----------------------------------------");
				System.out.println("Name: " + memb.getEffectiveName());
				System.out.println("Owner/Bot: " + memb.isOwner() + "/" + memb.getUser().isBot());
				System.out.println("----------------------------------------");
			}
			
			System.out.println("========================================");
		}
	}
	
	public void onShutdown() {
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
			this.mainDB.disconnect();
			this.loopDB.disconnect();
			this.listenerDB.disconnect();
			this.gameDB.disconnect();
			
			this.shutdownDB.disconnect();

			System.out.println("Bot is offline.");
		}
		if(this.loop != null) {
			this.loop.interrupt();
		}
	}
}
