package nl.marido.heads.volatilecode.reflection.nms;

import nl.marido.heads.volatilecode.reflection.ReflectObject;
import nl.marido.heads.volatilecode.reflection.ReflectionUtils;

public class Item extends ReflectObject {
    
    public static Class<?> ItemClass;
    
    static {
        ItemClass = ReflectionUtils.getNMSClass("Item");
    }
    
    public Item(Object handle) {
        super(handle);
    }
    
}
