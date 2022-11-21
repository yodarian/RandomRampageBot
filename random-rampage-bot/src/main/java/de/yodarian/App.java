package de.yodarian;

import java.io.IOException;

import de.yodarian.professions.ProfessionsCommand;
import de.yodarian.util.Helper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class App 
{
    public static void main( String[] args )
    {
        String status = "World of Warcraft";
        
        String BOT_TOKEN = null;
        try {
            BOT_TOKEN = Helper.getBotToken();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BOT_TOKEN != null)
        {
            JDABuilder blueprint = JDABuilder.createDefault(BOT_TOKEN);

            // Basics
            blueprint.setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing(status));
            
            // Permissions
            blueprint.setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGE_TYPING);

            // Events
            blueprint.addEventListeners(
                new ProfessionsCommand()
            );

            JDA bot = blueprint.build();
        }
    }
}
