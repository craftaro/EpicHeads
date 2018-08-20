package net.sothatsit.heads.volatilecode.reflection.craftbukkit;

import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

public class CommandMap {
    
    private static Field MapField;
    
    static {
        for (Field field : SimpleCommandMap.class.getDeclaredFields()) {
            if (field.getType().equals(Map.class)) {
                MapField = field;
                MapField.setAccessible(true);
                break;
            }
        }

        if(MapField == null) {
            new Exception("Could not find Map field in SimpleCommandMap").printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Map<String, Command> getCommandMap(SimpleCommandMap commands) {
        try {
            return (Map<String, Command>) MapField.get(commands);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
