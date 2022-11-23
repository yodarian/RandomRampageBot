package de.yodarian.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import de.yodarian.config.ProfessionsConfig;
import de.yodarian.setup.ProfessionsSetup;
import de.yodarian.util.Helper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
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
import net.dv8tion.jda.api.interactions.components.buttons.Button;
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
            case "mplus":
                handleMplusCommand(event);
                break;
            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
        }
    }

    private void handleMplusCommand(@NotNull SlashCommandInteractionEvent event)
    {
        MessageEmbed mplusEmbed = getMplusEmbed(event);

        Button tankButton = Button.secondary("tank", "Tank").withEmoji(Emoji.fromFormatted("<:tank:1044364540453867601>"));
        Button healButton = Button.secondary("heal", "Heal").withEmoji(Emoji.fromFormatted("<:heal:1044364577921568828>"));
        Button damageButton = Button.secondary("damage", "Damage").withEmoji(Emoji.fromFormatted("<:damage:1044364596649144452>"));
        
        Dotenv config = Dotenv.configure().directory("random-rampage-bot").load();
        String mplusChannelName = config.get("MPLUS_CHANNEL");
        List<TextChannel> channels = event.getGuild().getTextChannelsByName(mplusChannelName, false);
        if (!channels.isEmpty())
        {
            TextChannel mplusChannel = channels.get(0);
            mplusChannel.sendMessage("").addEmbeds(mplusEmbed).addActionRow(tankButton, healButton, damageButton).queue();
        } 
        else 
        {
            event.reply("")
                .addEmbeds(mplusEmbed)
                .addActionRow(tankButton, healButton, damageButton)
                .queue();
        }
        
        event.reply("M+ event was created").setEphemeral(true).queue();
        
    }

    private MessageEmbed getMplusEmbed(SlashCommandInteractionEvent event) 
    {
        String type = event.getOption("type").getAsString();
        String date = event.getOption("date").getAsString();
        String time = event.getOption("time").getAsString();
        String note = event.getOption("note").getAsString();
        Member member = event.getMember();

        EmbedBuilder blueprint = new EmbedBuilder();
        blueprint.setColor(0xa8d5fe)
                 .setTitle("**New M+ Event created by " + member.getEffectiveName() + "**")
                 .setThumbnail("https://cdn.discordapp.com/attachments/1040718388802105474/1044369536285147286/Mplus.PNG")
                 .setDescription(type.toUpperCase() + "\n" + date + " at " + time)
                 .addBlankField(false)
                 .addField("<:tank:1044364540453867601> Tank", "", true)
                 .addField("<:heal:1044364577921568828> Heal", "", true)
                 .addField("<:damage:1044364596649144452> Damage", "", true)
                 .addBlankField(false)
                 .addField("Special requests and notes", note, false);
                 
        return blueprint.build();
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
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED));

        commandData.add(Commands.slash("mplus", "Create new Mythic + event")
            .addOptions(
                new OptionData(OptionType.STRING, "type", "Type of M+ event to create")
                    .addChoice("Low Keys / Gearing", "low-keys")
                    .addChoice("Weekly Keys", "weekly-keys")
                    .addChoice("Pushing Keys", "pushing-keys")
                    .setRequired(true),
                new OptionData(OptionType.STRING, "date", "The date of the M+ event in the format DD-MM")
                    .setRequired(true),
                new OptionData(OptionType.STRING, "time", "The time of the M+ event in the format HH:mm")
                    .setRequired(true),
                new OptionData(OptionType.STRING, "note", "Special request and notes for this event")
                    .setRequired(false)
            )
            .setGuildOnly(true));

        guild.updateCommands().addCommands(commandData).queue();
    }
}
