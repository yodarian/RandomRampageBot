package de.yodarian.listeners;

import java.util.List;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MplusListener extends ListenerAdapter 
{
    public void onButtonInteraction(ButtonInteractionEvent event) 
    {
        List<MessageEmbed> msgEmbeds = event.getMessage().getEmbeds();
        MessageEmbed embed = msgEmbeds.get(0);

        List<Field> fields = embed.getFields();
        String note = "";
        String tank = "";
        String heal = "";
        String damage = "";
        boolean memberAlreadyAssigned = false;

        for (Field field : fields) 
        {
            if (field.getName().contains("note"))
            {
                note = field.getValue();
            }
            if (field.getName().contains("Tank"))
            {
                tank = field.getValue();
            }
            if (field.getName().contains("Heal"))
            {
                heal = field.getValue();
            }
            if (field.getName().contains("Damage"))
            {
                damage = field.getValue();
            }
        }

        if (event.getButton().getId().startsWith("tank")) 
        {
            if (tank.length() > 1)
            {
                if (tank.contains(event.getMember().getEffectiveName()))
                {
                    memberAlreadyAssigned = true;
                }
                tank += "\n";
            } 
            tank += "<:green_check_mark:1044559541037183016>" + event.getMember().getEffectiveName();
        } 
        else if (event.getButton().getId().startsWith("heal")) 
        {
            if (heal.length() > 1)
            {
                if (heal.contains(event.getMember().getEffectiveName()))
                {
                    memberAlreadyAssigned = true;
                }
                heal += "\n";
            }
            heal += "<:green_check_mark:1044559541037183016>" + event.getMember().getEffectiveName();
        }
        else if (event.getButton().getId().startsWith("damage"))  
        {
            if (damage.length() > 1)
            {
                if (damage.contains(event.getMember().getEffectiveName()))
                {
                    memberAlreadyAssigned = true;
                }
                damage += "\n";
            }
            damage += "<:green_check_mark:1044559541037183016>" + event.getMember().getEffectiveName();
        }

        if (!memberAlreadyAssigned)
        {
            EmbedBuilder blueprint = new EmbedBuilder();
                blueprint.setColor(0xa8d5fe)
                    .setTitle(embed.getTitle())
                    .setThumbnail("https://cdn.discordapp.com/attachments/1040718388802105474/1044369536285147286/Mplus.PNG")
                    .setDescription(embed.getDescription())
                    .addBlankField(false)
                    .addField("<:tank:1044364540453867601> Tank", tank, true)
                    .addField("<:heal:1044364577921568828> Heal", heal, true)
                    .addField("<:damage:1044364596649144452> Damage", damage, true)
                    .addBlankField(false)
                    .addField("Special requests and notes", note, false);
                
            event.editMessageEmbeds(blueprint.build()).queue();
        } 
        else
        {
            event.reply("You are already assigned to this event").setEphemeral(true).queue();
        }

    }
}
