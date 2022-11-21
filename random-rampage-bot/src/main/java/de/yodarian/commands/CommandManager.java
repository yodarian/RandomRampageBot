package de.yodarian.commands;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import de.yodarian.config.ProfessionsConfig;
import de.yodarian.util.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public class CommandManager extends ListenerAdapter
{
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) 
    {
        if (event.getGuild() == null)
            return;
            
        String command = event.getName();

        switch (command) 
        {
            case "rr-professions":
                handleProfessionsCommand(event);
                break;
            case "rr-setup":
                handleSetupCommand(event);
                break;
            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    private void handleSetupCommand(SlashCommandInteractionEvent event) 
    {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) 
        {
            event.reply("You need to be an admin to use this command").setEphemeral(true).queue();
            return;
        }

        OptionMapping option = event.getOption("step");
        String step = option.getAsString();
        Guild guild = event.getGuild();
        String[] professions = ProfessionsConfig.getProfessions();
        String categoryName = "Professions";

        switch (step) 
        {
            case "category-roles":
                setupCategory(guild, categoryName);
                setupRoles(guild, professions);
                event.reply("Category and Roles was setup successfully. Please run the command again to add Channels").setEphemeral(true).queue();
                break;
            case "channels":
                setupChannels(guild, professions, categoryName);
                event.reply("Channels were setup successfully").setEphemeral(true).queue();
                break;
            default:
                event.reply("I can't handle that option right now :(").setEphemeral(true).queue();
        }
    }

    private void setupChannels(Guild guild, String[] professions, String categoryName) 
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

    private void setupCategory(Guild guild, String categoryName)
    {
        List<Category> categories = guild.getCategoriesByName(categoryName, false);
        if (categories.isEmpty()) 
        {
            guild.createCategory(categoryName).queue();
        }
    }

    private void setupRoles(Guild guild, String[] professions) 
    {
        for (int i = 0; i < professions.length; i++) 
        {
            List<Role> roles = guild.getRolesByName(professions[i], false);
            if (roles.isEmpty()) {
                guild.createRole().setName(professions[i]).setPermissions(Permission.VIEW_CHANNEL).setMentionable(true).queue();
            }
        }
    }

    private void handleProfessionsCommand(SlashCommandInteractionEvent event) 
    {
        MessageEmbed msgembed = getProfessionChooseEmbed();
        StringSelectMenu menu = getProfessionSelectMenu(event.getJDA());
        
        event.reply("Please pick your professions below")
            .setEphemeral(true)
            .addEmbeds(msgembed)
            .addActionRow(menu)
            .queue();
    }

    public void onGuildReady(@NotNull GuildReadyEvent event)
    {
        addCommandsToGuild(event.getGuild());
    }

    public void onGuildJoin(@NotNull GuildJoinEvent event)
    {
        addCommandsToGuild(event.getGuild());
    }

    public void onStringSelectInteraction(StringSelectInteractionEvent event) 
    {
        if (event.getSelectMenu().getId().equals("menu:professions"))
        {
            Role role = null;
            Guild guild = event.getGuild();

            Map<String, String> roleConfigMap = this.getRoleConfigMap(event.getGuild());
            Map<String, String> notUsedRoles = Helper.getCopyOfMap(roleConfigMap);
            
            for (String s : event.getValues())
            {
                role = guild.getRoleById(roleConfigMap.get(s));
                guild.addRoleToMember(event.getUser(), role).queue();
                notUsedRoles.remove(s);
            }

            //System.out.println(notUsedRoles.toString());
            for(Map.Entry<String, String> entry : notUsedRoles.entrySet())
            {
                role = guild.getRoleById(roleConfigMap.get(entry.getKey()));
                guild.removeRoleFromMember(event.getUser(), role).queue();
            }

            event.reply("Your Professions were updated successfully!").setEphemeral(true).queue();
        }
    }

    private StringSelectMenu getProfessionSelectMenu(JDA jda) 
    {
        String professions[] = ProfessionsConfig.getProfessions();
        Map<String, String> emojiMap = ProfessionsConfig.getEmojiMap();

        String description = "Click to choose this profession!";

        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("menu:professions")
            .setPlaceholder("Choose your professions")
            .setRequiredRange(1, professions.length);

        for (int i = 0; i < professions.length; i++)
        {
            menuBuilder.addOption(Helper.ucfirst(professions[i]), professions[i], description, jda.getEmojiById(emojiMap.get(professions[i])));
            //menuBuilder.addOption(Helper.ucfirst(professions[i]), professions[i], description, Emoji.fromFormatted("<:tank:1040653392588062740>"));            
        }
            
        return menuBuilder.build();
    }

    private MessageEmbed getProfessionChooseEmbed() 
    {
        EmbedBuilder blueprint = new EmbedBuilder();
        blueprint.setColor(0xa8d5fe)
                 .setTitle("**:construction_worker: | Choose your professions!**")
                 .setDescription("Click the menu below to choose your professions")
                 .setThumbnail("https://cdn.discordapp.com/attachments/1042136257238667274/1043507733971865640/prof.jpg")
                 .setImage("https://cdn.discordapp.com/attachments/994209663316918382/994210394736447548/unknown.png");

        return blueprint.build();
    }

    private Map<String, String> getRoleConfigMap(@NotNull Guild guild)
    {
        Map<String, String> roleConfigMap = new HashMap<String, String>();
        String professions[] = ProfessionsConfig.getProfessions();
    
        for (int i = 0; i < professions.length; i++)
        {
            List<Role> roleList = guild.getRolesByName(professions[i], false);
            if (roleList.size() > 0)
            {
                String id = roleList.get(0).getId();
                roleConfigMap.put(professions[i], id);
            } else {
                System.out.println("Rolle **" + professions[i] + "** not found!");
            }
        }

        return roleConfigMap;
    }

    private void addCommandsToGuild(@NotNull Guild guild)
    {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("rr-professions", "Get menu to select professions")
           .setGuildOnly(true));        
        commandData.add(Commands.slash("rr-setup", "ADMIN: Setup channels and roles")
            .addOptions(
                new OptionData(OptionType.STRING, "step", "The setup step you want to execute")
                    .addChoice("1. Category and Roles", "category-roles")
                    .addChoice("2. Channels", "channels")
                    .setRequired(true)
            )            
            .setGuildOnly(true)
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
        );
        guild.updateCommands().addCommands(commandData).queue();
    }
}
