package net.sothatsit.heads.volatilecode.reflection.nms.nbt;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class NBTBase extends ReflectObject {
    
    public static Class<?> NBTBaseClass;
    
    static {
        NBTBaseClass = ReflectionUtils.getNMSClass("NBTBase");
    }
    
    public NBTBase(Object handle) {
        super(handle);
    }
    
}
