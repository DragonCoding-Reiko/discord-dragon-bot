package de.dragonbot.commands.mod.channel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;

public class Stats implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();
		//#d stats
		//#d stats delete

		if(m.hasPermission(Permission.ADMINISTRATOR)) {
			String[] args = message.getContentDisplay().substring(subString).split(" ");
			Guild guild = channel.getGuild();
			
			String sql_SELECT_StatsCategory = "SELECT `category_ID` "
					   					  + "FROM `Stats_Channels` "
					   					  + "WHERE guild_ID = " + guild.getIdLong();
			
			ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_StatsCategory);

			try {
				if(!set.next()) {

					Category category = guild.createCategory("Statistiken").complete();
					category.getManager().setPosition(1).queue();

					PermissionOverride override = new PermissionOverrideActionImpl(category.getJDA(), category, category.getGuild().getPublicRole()).complete();

					category.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();

					String sql_INSERT_NewStatsChannel = "INSERT INTO `Stats_Channels`(`guild_ID`, `category_ID`) "
							    			          + "VALUES (" + guild.getIdLong() + ", " + category.getIdLong() +")";
					
					DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewStatsChannel);

					fillCategory(category);
				}
				else {
					long categoryid = set.getLong("category_ID");
					channel.sendMessage("Kategorie geupdated.").complete().delete().queueAfter(5, TimeUnit.SECONDS);
					Category cat = guild.getCategoryById(categoryid);

					if(args.length == 2) {
						if(args[1].equalsIgnoreCase("delete")) {
							
							String sql_REMOVE_StatsChannel = "DELETE FROM `Stats_Channels` "
														   + "WHERE guild_ID = " + guild.getIdLong();
							
							DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_StatsChannel);

							cat.getChannels().forEach(chan -> {
								chan.delete().complete();
							});
							cat.delete().queue();

							return;
						}
					}

					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});

					fillCategory(cat);
				}
			} catch (SQLException e) {
				Utils.printError(e, null);
			}
		}
	}

	public static void fillCategory(Category cat) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		SimpleDateFormat df2 = new SimpleDateFormat("EEEE, dd.MM.YYYY");
		cat.createVoiceChannel("🕗  Uhrzeit: " + df.format(Calendar.getInstance().getTime()) + "Uhr").queue();
		cat.createVoiceChannel("📅  " + df2.format(Calendar.getInstance().getTime())).queue();

		List<Member> members = cat.getGuild().getMembers();
		int member = 0;
		for(Member memb : members) {
			if(!memb.getUser().isBot()) {
				member++;
			}
		}
		int online = 0;

		for(Member memb : members) {
			if(memb.getOnlineStatus() != OnlineStatus.OFFLINE) {
				if(!memb.getUser().isBot()) {
					online++;
				}
			}
		}
		cat.createVoiceChannel("🔘  Online User: " + online + " / " + member).queue();
		cat.createVoiceChannel("✅  BOT ONLINE").queue();

		PermissionOverride override = new PermissionOverrideActionImpl(cat.getJDA(), cat, cat.getGuild().getPublicRole()).complete();

		cat.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
	}

	public static void sync(Category cat) {
		cat.getChannels().forEach(chan -> {
			chan.getManager().sync().queue();
		});
	}

	public static void updateCategory(Category cat) {
		if(cat.getChannels().size() == 4) {
			sync(cat);
			List<GuildChannel> channels = cat.getChannels();
			SimpleDateFormat df = new SimpleDateFormat("HH:mm");
			SimpleDateFormat df2 = new SimpleDateFormat("EEEE, dd.MM.YYYY");

			channels.get(0).getManager().setName("🕗  Uhrzeit: " + df.format(Calendar.getInstance().getTime()) + "Uhr").queue();
			channels.get(1).getManager().setName("📅  " + df2.format(Calendar.getInstance().getTime())).queue();
			List<Member> members = cat.getGuild().getMembers();
			int member = 0;
			for(Member memb : members) {
				if(!memb.getUser().isBot()) {
					member++;
				}
			}
			int online = 0;

			for(Member memb : members) {
				if(memb.getOnlineStatus() != OnlineStatus.OFFLINE) {
					if(!memb.getUser().isBot()) {
						online++;
					}
				}
			}
			channels.get(2).getManager().setName("🔘  Online User: " + online + " / " + member).queue();
		}
	}

	public static void checkStats() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			
			String sql_SELECT_StatsCategory = "SELECT `category_ID` "
 					  						+ "FROM `Stats_Channels` "
 					  						+ "WHERE guild_ID = " + guild.getIdLong();
			
			ResultSet set = DragonBot.INSTANCE.loopDB.getData(sql_SELECT_StatsCategory);

			try {
				if(set.next()) {
					long catid = set.getLong("category_ID");
					Stats.updateCategory(guild.getCategoryById(catid));
				}
			} catch (SQLException e) {
				Utils.printError(e, sql_SELECT_StatsCategory);
			}
		}); 
	}

	public static void onStartUP() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			
			String sql_SELECT_StatsCategory = "SELECT `category_ID` "
					  						+ "FROM `Stats_Channels` "
					  						+ "WHERE guild_ID = " + guild.getIdLong();
			
			ResultSet set = DragonBot.INSTANCE.loopDB.getData(sql_SELECT_StatsCategory);

			try {
				if(set.next()) {
					long catid = set.getLong("category_ID");
					Category cat = guild.getCategoryById(catid);

					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});

					fillCategory(cat);
				}
			} catch (SQLException e) {
				Utils.printError(e, sql_SELECT_StatsCategory);
			}
		}); 
	}

	public static void onShutdown() {
		DragonBot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			String sql_SELECT_StatsCategory = "SELECT `category_ID` "
											+ "FROM `Stats_Channels` "
											+ "WHERE guild_ID = " + guild.getIdLong();

			ResultSet set = DragonBot.INSTANCE.loopDB.getData(sql_SELECT_StatsCategory);

			try {
				if(set.next()) {
					long catid = set.getLong("category_ID");
					Category cat = guild.getCategoryById(catid);

					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});

					VoiceChannel offline = cat.createVoiceChannel("🔴  BOT OFFLINE").complete();
					PermissionOverride override = new PermissionOverrideActionImpl(cat.getJDA(), offline, cat.getGuild().getPublicRole()).complete();

					offline.getManager().putPermissionOverride(override.getRole(), null, EnumSet.of(Permission.VOICE_CONNECT)).queue();
				}
			} catch (SQLException e) {
				Utils.printError(e, sql_SELECT_StatsCategory);
			}
		}); 
	}
}
