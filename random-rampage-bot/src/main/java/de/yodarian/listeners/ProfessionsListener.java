package de.yodarian.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import de.yodarian.config.ProfessionsConfig;
import de.yodarian.util.Helper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ProfessionsListener extends ListenerAdapter
{
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) 
    {
        if (event.getSelectMenu().getId().equals("menu:professions"))
        {
            Role role = null;
            Guild guild = event.getGuild();

            Map<String, String> roleConfigMap = this.getRoleConfigMap(guild);
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
}
