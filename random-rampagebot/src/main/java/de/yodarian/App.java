package de.yodarian;

import de.yodarian.professions.RoleAssignment;
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
        String BOT_TOKEN = "MTA0MTAyNTI4ODg5ODg5MTk3Nw.GEaaH8.VEfZ2GDj5hHAHDi3rw0xqCKKr72Nc-zpkeKF5I";

        JDABuilder blueprint = JDABuilder.createDefault(BOT_TOKEN);

        // Basics
        blueprint.setStatus(OnlineStatus.ONLINE)
                 .setActivity(Activity.playing(status));
        
        // Permissions
        blueprint.setChunkingFilter(ChunkingFilter.ALL)
                 .setMemberCachePolicy(MemberCachePolicy.ALL)
                 .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGE_TYPING);

        // Events
        blueprint.addEventListeners(new RoleAssignment());

        JDA bot = blueprint.build();
    }
}
