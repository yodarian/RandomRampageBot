package de.yodarian.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import de.yodarian.config.ProfessionsConfig;
import de.yodarian.setup.ProfessionsSetup;
import de.yodarian.util.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class CommandManager extends ListenerAdapter
{
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) 
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

    private void handleSetupCommand(@NotNull SlashCommandInteractionEvent event) 
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
                ProfessionsSetup.setupProfessionCategory(guild, categoryName);
                ProfessionsSetup.setupProfessionRoles(guild, professions);
                event.reply("Category and Roles was setup successfully. Please run the command again to add Channels").setEphemeral(true).queue();
                break;
            case "channels":
                ProfessionsSetup.setupProfessionChannels(guild, professions, categoryName);
                event.reply("Channels were setup successfully").setEphemeral(true).queue();
                break;
            default:
                event.reply("I can't handle that option right now :(").setEphemeral(true).queue();
        }
    }

    private void handleProfessionsCommand(@NotNull SlashCommandInteractionEvent event) 
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

    private StringSelectMenu getProfessionSelectMenu(@NotNull JDA jda) 
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
                 .setThumbnail("https://cdn.discordapp.com/attachments/1040718388802105474/1044334114238648452/prof.jpg")
                 .setImage("https://cdn.discordapp.com/attachments/994209663316918382/994210394736447548/unknown.png");

        return blueprint.build();
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
