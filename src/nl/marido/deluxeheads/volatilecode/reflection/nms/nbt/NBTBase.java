package nl.marido.deluxeheads.volatilecode.reflection.nms.nbt;

import nl.marido.deluxeheads.volatilecode.reflection.ReflectObject;
import nl.marido.deluxeheads.volatilecode.reflection.ReflectionUtils;

public class NBTBase extends ReflectObject {
    
    public static Class<?> NBTBaseClass;
    
    static {
        NBTBaseClass = ReflectionUtils.getNMSClass("NBTBase");
    }
    
    public NBTBase(Object handle) {
        super(handle);
    }
    
}
