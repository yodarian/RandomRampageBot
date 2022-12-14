package de.yodarian.config;

import java.util.HashMap;
import java.util.Map;

public class ProfessionsConfig 
{
    //@Todo use List or something else?
    public static String professions[] = {
        "herbalism", 
        "mining", 
        "skinning", 
        "fishing", 
        "cooking", 
        "alchemy", 
        "inscription", 
        "engineering", 
        "enchanting", 
        "blacksmithing",
        "jewelcrafting",
        "leatherworking", 
        "tailoring",
    };

    public static String[] getProfessions() 
    { 
        return professions; 
    }

    public static Map<String, String> getEmojiMap()
    {
        Map<String, String> emojiMap = new HashMap<String, String>();

        emojiMap.put("herbalism", "1043310442656825394");
        emojiMap.put("mining", "1043310500139782175");
        emojiMap.put("skinning", "1043310523640455168");
        emojiMap.put("fishing", "1043310418409574541");
        emojiMap.put("cooking", "1043310351296516126");
        emojiMap.put("alchemy", "1043300521978630214");
        emojiMap.put("inscription", "1043310460402925629");
        emojiMap.put("engineering", "1043310385769484328");
        emojiMap.put("enchanting", "1043310372586782771");
        emojiMap.put("blacksmithing", "1043310294539186196");
        emojiMap.put("jewelcrafting", "1043924279416344686");
        emojiMap.put("leatherworking", "1043310479482826812");
        emojiMap.put("tailoring", "1043310539616567317");

        return emojiMap;
    }
}
