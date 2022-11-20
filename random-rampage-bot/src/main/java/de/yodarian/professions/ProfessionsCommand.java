package de.yodarian.professions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import de.yodarian.util.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class ProfessionsCommand extends ListenerAdapter
{
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) 
    {
        String command = event.getName();

        if (command.equals("rr-professions") ) 
        {
            MessageEmbed msgembed = getProfessionChooseEmbed();
            StringSelectMenu menu = getProfessionSelectMenu(event.getJDA());
            
            event.reply("Please pick your professions below")
            .setEphemeral(true)
            .addEmbeds(msgembed)
            .addActionRow(menu)
            .queue();
        }
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
        String professions[] = Config.getProfessions();
        Map<String, String> emojiMap = Config.getEmojiMap();

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
        String professions[] = Config.getProfessions();
    
        for (int i = 0; i < professions.length; i++)
        {
            List<Role> roleList = guild.getRolesByName(professions[i], false);
            if (roleList.size() > 0)
            {
                String id = roleList.get(0).getId();
                roleConfigMap.put(professions[i], id);
            } else {
                System.out.println("Rolle **" + professions[i] + "** nicht gefunden!");
            }
            
            
        }

        return roleConfigMap;
    }

    private void addCommandsToGuild(@NotNull Guild guild)
    {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("rr-professions", "Get menu to select professions"));
        guild.updateCommands().addCommands(commandData).queue();
    }
}
