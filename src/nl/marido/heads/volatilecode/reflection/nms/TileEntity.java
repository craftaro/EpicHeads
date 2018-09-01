package nl.marido.heads.volatilecode.reflection.nms;

import nl.marido.heads.volatilecode.reflection.ReflectObject;
import nl.marido.heads.volatilecode.reflection.ReflectionUtils;

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
