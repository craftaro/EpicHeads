package nl.marido.heads.volatilecode.reflection.nms.nbt;

import nl.marido.heads.volatilecode.reflection.ReflectObject;
import nl.marido.heads.volatilecode.reflection.ReflectionUtils;

public class NBTBase extends ReflectObject {
    
    public static Class<?> NBTBaseClass;
    
    static {
        NBTBaseClass = ReflectionUtils.getNMSClass("NBTBase");
    }
    
    public NBTBase(Object handle) {
        super(handle);
    }
    
}
