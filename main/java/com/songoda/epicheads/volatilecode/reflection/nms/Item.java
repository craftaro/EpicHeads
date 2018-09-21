package com.songoda.epicheads.volatilecode.reflection.nms;

import com.songoda.epicheads.volatilecode.reflection.ReflectObject;
import com.songoda.epicheads.volatilecode.reflection.ReflectionUtils;

public class Item extends ReflectObject {
    
    public static Class<?> ItemClass;
    
    static {
        ItemClass = ReflectionUtils.getNMSClass("Item");
    }
    
    public Item(Object handle) {
        super(handle);
    }
    
}
