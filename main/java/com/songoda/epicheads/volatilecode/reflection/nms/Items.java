package com.songoda.epicheads.volatilecode.reflection.nms;

import com.songoda.epicheads.volatilecode.reflection.ReflectObject;
import com.songoda.epicheads.volatilecode.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Items extends ReflectObject {
    
    public static Class<?> ItemsClass;
    public static Map<String, Field> cachedFields = new HashMap<>();
    
    static {
        ItemsClass = ReflectionUtils.getNMSClass("Items");
    }
    
    public Items(Object handle) {
        super(handle);
    }
    
    public static Item getItem(String name) {
        Field f = cachedFields.get(name);
        
        if (f == null) {
            for (Field field : ItemsClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) && field.getName().equals(name)) {
                    f = field;
                    f.setAccessible(true);
                    cachedFields.put(name, f);
                    break;
                }
            }
        }
        
        if (f == null) {
            return null;
        }
        
        try {
            return new Item(f.get(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
