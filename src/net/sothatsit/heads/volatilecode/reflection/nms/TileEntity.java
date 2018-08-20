package net.sothatsit.heads.volatilecode.reflection.nms;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

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
