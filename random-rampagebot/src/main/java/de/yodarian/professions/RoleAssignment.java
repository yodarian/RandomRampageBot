package de.yodarian.professions;

import java.util.HashMap;
import java.util.Map;

import de.yodarian.util.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class RoleAssignment extends ListenerAdapter
{    
    public void onMessageReceived (MessageReceivedEvent event)
    {
        String msgStripped = event.getMessage().getContentStripped();

        if (msgStripped.equals("!profession roles") || msgStripped.equals("!pr")) 
        {
            MessageEmbed msgembed = getProfessionChooseEmbed();
            
            StringSelectMenu menu = getProfessionSelectMenu(event.getJDA());

            event.getChannel().sendMessageEmbeds(msgembed).setActionRow(menu).queue();
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
                 .setDescription("Click the menu below to choose your professions");

        return blueprint.build();
    }

    public void onStringSelectInteraction(StringSelectInteractionEvent event) 
    {
        if (event.getSelectMenu().getId().equals("menu:professions"))
        {
            Role role = null;
            Guild guild = event.getGuild();

            Map<String, String> roleConfigMap = this.getRoleConfigMap();
            Map<String, String> notUsedRoles = Helper.getCopyOfMap(roleConfigMap);
            
            for (String s : event.getValues())
            {
                role = guild.getRoleById(roleConfigMap.get(s));
                guild.addRoleToMember(event.getUser(), role).queue();
                notUsedRoles.remove(s);
            }

            System.out.println(notUsedRoles.toString());
            for(Map.Entry<String, String> entry : notUsedRoles.entrySet())
            {
                role = guild.getRoleById(roleConfigMap.get(entry.getKey()));
                guild.removeRoleFromMember(event.getUser(), role).queue();
            }

            event.reply("Your Professions were updated successfully!").setEphemeral(true).queue();
        }
    }

    private Map<String, String> getRoleConfigMap()
    {
        Map<String, String> roleConfigMap = new HashMap<String, String>();
            roleConfigMap.put("herbalism", "1042138228502843472");
            roleConfigMap.put("mining", "1042140476553310290");
            roleConfigMap.put("skinning", "1042140537953714206");
            roleConfigMap.put("fishing", "1042140623655948358");
            roleConfigMap.put("alchemy", "1042140883400806490");
            roleConfigMap.put("inscription", "1042140939784826942");
            roleConfigMap.put("engineering", "1042141007153729578");
            roleConfigMap.put("enchanting", "1042141061004394517");
            roleConfigMap.put("blacksmithing", "1042141103417200721");
            roleConfigMap.put("leatherworking", "1042141174330298459");
            roleConfigMap.put("tailoring", "1042141221063233607");

            return roleConfigMap;
    }

     
}
