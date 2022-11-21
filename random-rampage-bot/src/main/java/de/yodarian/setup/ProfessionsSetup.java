package de.yodarian.setup;

import java.util.EnumSet;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class ProfessionsSetup 
{
    public static void setupProfessionCategory(@NotNull Guild guild, @NotNull String categoryName) 
    {
        List<Category> categories = guild.getCategoriesByName(categoryName, false);
        if (categories.isEmpty()) 
        {
            guild.createCategory(categoryName).queue();
        }
    }

    public static void setupProfessionRoles(@NotNull Guild guild, @NotNull String[] professions)
    {
        for (int i = 0; i < professions.length; i++) 
        {
            List<Role> roles = guild.getRolesByName(professions[i], false);
            if (roles.isEmpty()) 
            {
                guild.createRole()
                    .setName(professions[i])
                    .setPermissions(Permission.VIEW_CHANNEL)
                    .setMentionable(true)
                    .queue();
            }
        }
    }

    public static void setupProfessionChannels(@NotNull Guild guild, @NotNull String[] professions, @NotNull String categoryName) 
    {
        List<Category> categories = guild.getCategoriesByName(categoryName, false);
        if (!categories.isEmpty()) 
        {
            Category professionCategory = categories.get(0);                    
            EnumSet<Permission> permissions = EnumSet.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND);

            List<TextChannel> channels = guild.getTextChannelsByName("professions", true);
            if (channels.isEmpty()) {
                professionCategory.createTextChannel("professions").queue();
            }

            for (int i = 0; i < professions.length; i++) 
            {
                List<TextChannel> textChannels = guild.getTextChannelsByName(professions[i], true);
                if (!textChannels.isEmpty()) {
                    //@Todo check if the channel is the correct one (in category Professions)
                    System.out.println("Channel **" + professions[i] + "** already setup");
                    //@Todo make sure permissions are set correctly...
                } else {
                    //@Todo check if this is the correct role
                    List<Role> roles = guild.getRolesByName(professions[i], false);
                    if (!roles.isEmpty()) {
                        Role role = roles.get(0);
                        ChannelAction<TextChannel> manager = professionCategory.createTextChannel(professions[i])
                            .addPermissionOverride(role, permissions, null)
                            .addPermissionOverride(guild.getPublicRole(), null, permissions);
                        manager.queue();
                    } else {
                        System.out.println("Role missing for channel **" + professions[i] + "**");
                    }
                }
            }
        }
    }
}
