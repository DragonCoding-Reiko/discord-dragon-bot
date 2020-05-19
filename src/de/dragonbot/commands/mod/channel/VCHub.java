package de.dragonbot.commands.mod.channel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import de.dragonbot.DragonBot;
import de.dragonbot.commands.ServerCommand;
import de.dragonbot.manage.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.internal.requests.restaction.PermissionOverrideActionImpl;

public class VCHub implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message, int subString) {
		message.delete().queue();
		//#d vchub create <name> <places>
		//#d vchub delete <name>

		String[] args = message.getContentDisplay().substring(subString).split(" ");

		Guild guild = channel.getGuild();

		//Create VC Hub
		if(args[1].equalsIgnoreCase("create")) {

			VoiceChannel vc = guild.createVoiceChannel(args[2]).complete();
			Category cat = null;

			PermissionOverride override = new PermissionOverrideActionImpl(guild.getJDA(), vc, guild.getPublicRole()).complete();
			vc.getManager().putPermissionOverride(override.getPermissionHolder(), EnumSet.of(Permission.VOICE_CONNECT), null);

			if(args.length == 4) {
				vc.getManager().setUserLimit(Integer.parseInt(args[3]));
			}

			try {
				cat = guild.getCategoriesByName("VoiceHub", true).get(0);
			} catch (IndexOutOfBoundsException e) {
				Utils.printError(e, null);
			};

			if(cat == null) {
				cat = guild.createCategory("VoiceHub").complete();
				cat.getManager().setPosition(0).complete();
			}
			vc.getManager().setParent(cat).complete();

			String sql_INSERT_NewVCHub = "INSERT INTO `Voice_Channel_Hubs`(`guild_ID`, `category_ID`, `channel_ID`) "
									   + "VALUES (" + guild.getIdLong() + ", " + cat.getIdLong() + ", " + vc.getIdLong() + ")";
			
			DragonBot.INSTANCE.mainDB.execute(sql_INSERT_NewVCHub);
		}
		//Delete a VC Hub
		else if(args[1].equalsIgnoreCase("delete")) {

			VoiceChannel vc = guild.getVoiceChannelsByName(args[2], true).get(0);

			String sql_SELECT_VCHub = "SELECT `ID` "
									+ "FROM `Voice_Channel_Hubs` "
									+ "WHERE guild_ID = " + guild.getIdLong() + " AND channel_ID = " + vc.getIdLong();
			
			ResultSet set = DragonBot.INSTANCE.mainDB.getData(sql_SELECT_VCHub);

			try {
				while(set.next()) {
					
					String sql_REMOVE_VCHub = "DELETE FROM `Voice_Channel_Hubs` "
											+ "WHERE guild_ID = " + guild.getIdLong() + " AND channel_ID = " + vc.getIdLong();
					
					DragonBot.INSTANCE.mainDB.execute(sql_REMOVE_VCHub);
				}
			} catch (SQLException e) { 
				Utils.printError(e, null);
			}

			vc.delete().complete();

			Category cat = guild.getCategoriesByName("VoiceHub", true).get(0);
			int channels = 0;

			try {
				channels = cat.getChannels().size();
			} catch (NullPointerException e) { 
				Utils.printError(e, null);
			}

			if(channels == 1) {
				cat.delete().complete();
			}
		}
	}

}
