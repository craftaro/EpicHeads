package com.songoda.epicheads.volatilecode.reflection.nms;

import com.songoda.epicheads.volatilecode.reflection.ReflectObject;
import com.songoda.epicheads.volatilecode.reflection.ReflectionUtils;

public class TileEntity extends ReflectObject {
    
    public static Class<?> TileEntityClass;
    
    static {
        TileEntityClass = ReflectionUtils.getNMSClass("TileEntity");
    }
    
    public TileEntity(Object handle) {
        super(handle);
    }
    
    public TileEntitySkull asSkullEntity() {
        return new TileEntitySkull(handle);
    }
    
}
