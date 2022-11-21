package de.yodarian;

import java.io.IOException;

import de.yodarian.professions.ProfessionsCommand;
import de.yodarian.util.Helper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class RandomRampageBot 
{
    private final Dotenv config;
    private final ShardManager shardManager;

    public RandomRampageBot()
    {
        config = Dotenv.configure().directory("random-rampage-bot").load();
        String token = config.get("BOT_TOKEN");

        DefaultShardManagerBuilder blueprint = DefaultShardManagerBuilder.createDefault(token);

        // Basics
        blueprint.setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("World of Warcraft"));
        
        // Permissions
        blueprint.setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGE_TYPING);

        // Events
        blueprint.addEventListeners(
            new ProfessionsCommand()
        );

        shardManager = blueprint.build();
    }

    public Dotenv getConfig()
    {
        return config;
    }

    public ShardManager getShardManager() 
    {
        return shardManager;
    }
    public static void main(String[] args)
    {   
        try {
            RandomRampageBot bot = new RandomRampageBot();
        } catch (InvalidTokenException ite) {
            ite.printStackTrace();
            System.exit(1);
        }
       
    }
}
