package net.sothatsit.heads.volatilecode.reflection.nms;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class Item extends ReflectObject {
    
    public static Class<?> ItemClass;
    
    static {
        ItemClass = ReflectionUtils.getNMSClass("Item");
    }
    
    public Item(Object handle) {
        super(handle);
    }
    
}
