package de.yodarian.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MplusListener extends ListenerAdapter 
{
    public void onButtonInteraction(ButtonInteractionEvent event) 
    {
        String buttonId = event.getButton().getId();

        if (buttonId.startsWith("tank") || buttonId.startsWith("heal") || buttonId.startsWith("damage"))
        {
            List<MessageEmbed> msgEmbeds = event.getMessage().getEmbeds();
            MessageEmbed embed = msgEmbeds.get(0);
            
            String memberName = event.getMember().getEffectiveName();
            boolean memberAlreadyAssigned = false;

            Map<String, String> embedFieldValuesMap = getFieldValuesMap(embed.getFields());

            if (buttonId.startsWith("tank")) 
            {
                if (embedFieldValuesMap.get("tank").length() > 1)
                {
                    if (embedFieldValuesMap.get("tank").contains(memberName))
                    {
                        memberAlreadyAssigned = true;
                    }
                    embedFieldValuesMap.put("tank", embedFieldValuesMap.get("tank") + "\n");
                } 
                embedFieldValuesMap.put("tank", embedFieldValuesMap.get("tank") + "<:green_check_mark:1044559541037183016>" + memberName);
            } 
            else if (buttonId.startsWith("heal")) 
            {
                if (embedFieldValuesMap.get("heal").length() > 1)
                {
                    if (embedFieldValuesMap.get("heal").contains(memberName))
                    {
                        memberAlreadyAssigned = true;
                    }
                    embedFieldValuesMap.put("heal", embedFieldValuesMap.get("heal") + "\n");
                }
                embedFieldValuesMap.put("heal", embedFieldValuesMap.get("heal") + "<:green_check_mark:1044559541037183016>" + memberName);
            }
            else if (buttonId.startsWith("damage"))  
            {
                if (embedFieldValuesMap.get("damage").length() > 1)
                {
                    if (embedFieldValuesMap.get("damage").contains(memberName))
                    {
                        memberAlreadyAssigned = true;
                    }
                    embedFieldValuesMap.put("damage", embedFieldValuesMap.get("heal") + "\n");
                }
                embedFieldValuesMap.put("damage", embedFieldValuesMap.get("damage") + "<:green_check_mark:1044559541037183016>" + memberName);
            }

            if (!memberAlreadyAssigned)
            {
                EmbedBuilder blueprint = new EmbedBuilder();
                    blueprint.setColor(0xa8d5fe)
                        .setTitle(embed.getTitle())
                        .setThumbnail("https://cdn.discordapp.com/attachments/1040718388802105474/1044369536285147286/Mplus.PNG")
                        .setDescription(embed.getDescription())
                        .addBlankField(false)
                        .addField("<:tank:1044364540453867601> Tank", embedFieldValuesMap.get("tank"), true)
                        .addField("<:heal:1044364577921568828> Heal", embedFieldValuesMap.get("heal"), true)
                        .addField("<:damage:1044364596649144452> Damage", embedFieldValuesMap.get("damage"), true)
                        .addBlankField(false)
                        .addField("Special requests and notes", embedFieldValuesMap.get("note"), false);
                    
                event.editMessageEmbeds(blueprint.build()).queue();
            } 
            else
            {
                event.reply("You are already assigned to this event with that role").setEphemeral(true).queue();
            }
        }
    }

    private Map<String, String> getFieldValuesMap(List<Field> fields) 
    {
        Map<String, String> embedFieldValuesMap = new HashMap<String, String>();

        for (Field field : fields) 
        {
            if (field.getName().contains("note"))
            {
                embedFieldValuesMap.put("note", field.getValue());
            }
            if (field.getName().contains("Tank"))
            {
                embedFieldValuesMap.put("tank", field.getValue());
            }
            if (field.getName().contains("Heal"))
            {
                embedFieldValuesMap.put("heal", field.getValue());
            }
            if (field.getName().contains("Damage"))
            {
                embedFieldValuesMap.put("damage", field.getValue());
            }
        }
        return embedFieldValuesMap;
    }
}
