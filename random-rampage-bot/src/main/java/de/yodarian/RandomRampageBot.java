package de.yodarian;

import de.yodarian.commands.CommandManager;
import de.yodarian.listeners.MplusListener;
import de.yodarian.listeners.ProfessionsListener;
import io.github.cdimascio.dotenv.Dotenv;
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

        // Basic Settings
        blueprint.setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("World of Warcraft"));
        
        // Permissions and Cacheing
        // @Todo What do I really need for the bot to function properly?
        /* blueprint.setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(
                    GatewayIntent.GUILD_MEMBERS, 
                    GatewayIntent.MESSAGE_CONTENT, 
                    GatewayIntent.GUILD_MESSAGES, 
                    GatewayIntent.GUILD_PRESENCES, 
                    GatewayIntent.GUILD_VOICE_STATES, 
                    GatewayIntent.DIRECT_MESSAGE_TYPING
                ); */

        // Add Events
        blueprint.addEventListeners(
            new CommandManager(),
            new ProfessionsListener(),
            new MplusListener()
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
