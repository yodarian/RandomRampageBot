package de.yodarian.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Helper 
{
    public static String ucfirst(String inputString)
    {
        if (inputString == null || inputString.length() == 0)
        {
            return inputString;
        }

        StringBuilder sb = new StringBuilder(inputString);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static Map<String, String> getCopyOfMap(Map<String, String> map)
    {
        Map<String, String> copy = new HashMap<String, String>();
        copy.putAll(map);
        return copy;
    }

    public static String getBotToken() throws IOException
    {
        String workingDir = System.getProperty("user.dir");
        Path filePath = Paths.get(workingDir + File.separator + "random-rampage-bot/src/main/java/de/yodarian/config/dc.token");

        return Files.readString(filePath);
    }
    
}
