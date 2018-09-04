package nl.marido.deluxeheads.volatilecode.reflection.nms;

import nl.marido.deluxeheads.volatilecode.reflection.ReflectObject;
import nl.marido.deluxeheads.volatilecode.reflection.ReflectionUtils;

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
