package com.songoda.epicheads.volatilecode.reflection.nms.nbt;

import com.songoda.epicheads.volatilecode.reflection.ReflectObject;
import com.songoda.epicheads.volatilecode.reflection.ReflectionUtils;

public class NBTBase extends ReflectObject {
    
    public static Class<?> NBTBaseClass;
    
    static {
        NBTBaseClass = ReflectionUtils.getNMSClass("NBTBase");
    }
    
    public NBTBase(Object handle) {
        super(handle);
    }
    
}
