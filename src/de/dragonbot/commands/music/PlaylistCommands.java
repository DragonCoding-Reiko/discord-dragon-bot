package de.dragonbot.commands.music;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import de.dragonbot.music.AudioLoadResult;
import de.dragonbot.music.MusicController;
import de.dragonbot.music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlaylistCommands implements ServerCommand{

	private String[] args;
	
	
	private Guild guild;
	private Long guild_ID;
	private Member member;
	private Long member_ID;
	private TextChannel channel;
	private String cmd;
	private String playlist_Name;
	
	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();
		
		this.args = message.getContentDisplay().substring(subString).split(" ");
		this.cmd = args[1].toLowerCase();
		this.playlist_Name = args[2];
		
		this.guild = channel.getGuild();
		this.guild_ID = guild.getIdLong();
		this.member = m;
		this.member_ID = member.getIdLong();
		this.channel = channel;
		
		switch (cmd) {
		case "create":
			createPlaylist();
			break;
			
		case "delete":
			deletePlaylist();
			break;
			
		case "update":
			updatePlaylist();
			break;
			
		case "play":
			playPlaylist();
			break;
			
		case "add":
			addPlaylist();
			break;
			
		default:
			break;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void createPlaylist() {
		String sql_SELECT_PlaylistCount = "SELECT `COUNT(*)` "
										+ "FROM `Playlists` "
										+ "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID;
		
		ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_PlaylistCount);
		int playLists = -1;
		
		try {
			
			if(set != null) {
				playLists = set.getInt(1);
			}
			
			if(playLists > -1 && playLists < 2) {
				//Test if the playlist has the same Name as the first of that Member
				
				String sql_SELECT_PlaylistName = "SELECT `playlist_Name` "
											   + "FROM `Playlists` "
											   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID;
				set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_PlaylistName);
				if(playLists == 1) {
					if(args[2].equalsIgnoreCase(set.getString("playlist_Name"))) {
						//Error - Same Name (Guild Player)
						return;
					}
				}
				
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(guild_ID);
				Queue queue = controller.getQueue();
				
				JSONArray trackList = new JSONArray();
				
				for(AudioTrack track : queue.getQueueList()) {
					if(track.getInfo().isStream) continue;
					
					JSONObject trackInfo = new JSONObject();
					trackInfo.put("url", track.getInfo().uri);
				}
				
				//PrettyPrint JSON
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String finishedJSON = gson.toJson(trackList);
				
				//Write JSON file
		    	File file = new File(buildPath());
		    	System.out.println(file.getAbsolutePath());
				file.getParentFile().mkdirs();
				
		    	FileWriter writer = new FileWriter(file);
		    	
		    	writer.write(finishedJSON);
		    	writer.flush();
		    	
		    	writer.close();
 
		    	String sql_INSERT_NewPlaylist = "INSERT INTO `Playlists`(`guild_ID`, `creator_ID`, `playlist_Name`) "
		    								  + "VALUES (" + guild_ID + ", " + member_ID + ", '" + playlist_Name + "')";
		    	
		    	DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewPlaylist);
		    	
		    	ConsoleOutput();
		    	System.out.println("");
			}
		} catch (SQLException | IOException e) {
			Utils.printError(e, null);
		}
	}
	
	private void deletePlaylist() {
		String sql_SELECT_Playlist = "SELECT `ID` "
								   + "FROM `Playlists` "
								   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID + " AND playlist_Name = '" + playlist_Name + "'";
		
		ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_Playlist);
		
		try {
			if(set.next()) {
				
				String sql_REMOVE_Playlist = "DELETE FROM `Playlists` "
										   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID + " AND playlist_Name = '" + playlist_Name + "'";
				
				DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_Playlist);
				
				File oldFile = new File(buildPath());
				File archiveFile = new File(buildDeletedPath());
				
				archiveFile.getParentFile().mkdirs();
				
				oldFile.renameTo(archiveFile);
				
				ConsoleOutput();
		    	System.out.println("Moved to Archive - Path: " + buildDeletedPath());
		    	System.out.println("");
				
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
					
	}
	
	@SuppressWarnings("unchecked")
	private void updatePlaylist() {
		String sql_SELECT_Playlist = "SELECT `ID` "
				   				   + "FROM `Playlists` "
				   				   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID + " AND playlist_Name = '" + playlist_Name + "'";

		ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_Playlist);
		
		try {
			if(set.next()) {		
				//Delete the Playlist
				String sql_REMOVE_Playlist = "DELETE FROM `Playlists` "
						   				   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID + " AND playlist_Name = '" + playlist_Name + "'";

				DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_Playlist);
				
				File oldFile = new File(buildPath());
				File archiveFile = new File(buildChangedPath());
				
				archiveFile.getParentFile().mkdirs();
				
				oldFile.renameTo(archiveFile);

				//Rewrite the Playlist
				MusicController controller = DragonBot.INSTANCE.playerManager.getController(guild_ID);
				Queue queue = controller.getQueue();
				
				JSONArray trackList = new JSONArray();
				
				int i = 0;
				for(AudioTrack track : queue.getQueueList()) {
					if(track.getInfo().isStream) continue;
					
					i++;
					JSONObject trackInfo = new JSONObject();
					trackInfo.put("url", track.getInfo().uri);
			         
			        trackList.add( i, trackInfo);
				}
				
				//PrettyPrint JSON
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String finishedJSON = gson.toJson(trackList);
				
				//Write JSON file
			    	File newFile = new File(buildPath());
			    	newFile.getParentFile().mkdirs();
					
			    	FileWriter writer;

					writer = new FileWriter(newFile);
					
					writer.write(finishedJSON);
			    	writer.flush();
			    	
			    	writer.close();

			    	String sql_INSERT_NewPlaylist = "INSERT INTO `Playlists`(`guild_ID`, `creator_ID`, `playlist_Name`) "
			    								  + "VALUES (" + guild_ID + ", " + member_ID + ", '" + playlist_Name + "')";
			    	
			    	DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewPlaylist);
			    	
			    	ConsoleOutput();
			    	System.out.println("Old Copy(Archived) - Path: " + buildChangedPath());
			    	System.out.println("");
			    }
		} catch (SQLException | IOException e) {
			Utils.printError(e, null);
		}
	}

	private void playPlaylist() {
		String sql_SELECT_Playlist = "SELECT `ID` "
								   + "FROM `Playlists` "
								   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID + " AND playlist_Name = '" + playlist_Name + "'";

		ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_Playlist);
		
		try {
			if(set.next()) {
				GuildVoiceState state;
				if((state = member.getVoiceState()) != null) {
					VoiceChannel vc;
					if((vc = state.getChannel()) != null) {
						MusicController controller = DragonBot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
						AudioPlayerManager apm = DragonBot.INSTANCE.audioPlayermanager;
						AudioManager manager = vc.getGuild().getAudioManager();
						Queue queue = controller.getQueue();
						AudioPlayer player = controller.getPlayer();
						
						//Clear the Queue -> The playlist has all Trackslots (curr. max. = 500 Tracks)
						queue.onPlaylist();
						
						manager.openAudioConnection(vc);
						Utils.setMusicChannel(channel);

						JSONParser jsonParser = new JSONParser();
						
						FileReader reader = new FileReader(buildPath());

						EmbedBuilder builder = new EmbedBuilder();
						Object obj = jsonParser.parse(reader);
						JSONArray trackList = (JSONArray) obj;
					
						boolean oneTrack = trackList.size() < 2;
						int added = 0;
						int i = 0;
						for (Object newObj : trackList) {
							JSONObject jsonObject = (JSONObject) newObj;
						
							//get fieds and send the Titel zur Queue hinzugefügt
							String title = jsonObject.get("title").toString();
							String url = jsonObject.get("url").toString();
							
							controller.SetLoadedFromInternalPlaylist(true);
							apm.loadItem(url, new AudioLoadResult(controller));
							
							
							if(!oneTrack) {
								added++;
								if(i == 0) {
									if(player.getPlayingTrack() != null) player.getPlayingTrack().stop();
								}
								i++;
							}
							else 
							{
								builder.setDescription("Titel zu Queue hinzugefügt: \n [" 
										+ title + "](" + url + ")");
								player.getPlayingTrack().stop();
							}
						}
						
						if(!oneTrack) {
							builder.setDescription(added + " Titel zu Queue hinzugefügt.");
						}
						
						Utils.sendEmbed(builder, channel, 5l, null);
					}
					else {
						Utils.sendEmbed("ERROR", "Bitte joine einem VoiceChannel, um diesen Command zu benutzen.", channel, 3l, new Color(0xff0000));
					}
				}
				else {
					Utils.sendEmbed("ERROR", "Bitte joine einem VoiceChannel, um diesen Command zu benutzen.", channel, 3l, new Color(0xff0000));
				}
			}
		} catch (SQLException | ParseException | IOException e) {
			Utils.printError(e, null);
		}
	}
	
	private void addPlaylist() {
		String sql_SELECT_Playlist = "SELECT `ID` "
				   				   + "FROM `Playlists` "
				   				   + "WHERE guild_ID = " + guild_ID + " AND creator_ID = " + member_ID + " AND playlist_Name = '" + playlist_Name + "'";

		ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_Playlist);
		
		try {
			if(set.next()) {
				
			}
		} catch (SQLException e) {
			Utils.printError(e, null);
		}
		
	}
	
	private String buildPath() {
		String path = "./playlists/" + guild_ID + "/" + member_ID + "/"+ playlist_Name + ".json";
		
		return path;
	}
	
	private String buildChangedPath() {
		String oldPath = "./playlists/Archive/Changed/" + guild_ID + "_" + member_ID + "_" + playlist_Name + ".json";
		
		return oldPath;
	}
	
	private String buildDeletedPath() {
		String oldPath = "./playlists/Archive/Deleted/" + guild_ID + "_" + member_ID + "_" + playlist_Name + ".json";
		
		return oldPath;
	}
	
	private void ConsoleOutput() {
		System.out.println("");
    	System.out.println(cmd.toUpperCase() + " PLAYLIST: ");
    	System.out.println("-----------------------");
    	System.out.println("Guild_ID, Guild_Name: " + guild_ID + ", " + guild.getName());
    	System.out.println("Creator_ID: " + member_ID);
    	System.out.println("Name: " + playlist_Name);
    	System.out.println("Path: " + buildPath());
	}
}
