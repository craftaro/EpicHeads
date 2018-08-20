package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class World extends ReflectObject {
    
    public static Class<?> CraftWorldClass;
    public static Method getHandleMethod;
    
    public static Class<?> WorldClass;
    public static Method getTileEntityMethod;
    
    static {
        CraftWorldClass = ReflectionUtils.getCraftBukkitClass("CraftWorld");
        
        getHandleMethod = ReflectionUtils.getMethod(CraftWorldClass, "getHandle");
        
        WorldClass = ReflectionUtils.getNMSClass("World");
        
        getTileEntityMethod = ReflectionUtils.getMethod(WorldClass, "getTileEntity");
    }
    
    public World(org.bukkit.World world) {
        super(getHandle(world));
    }
    
    public World(Object handle) {
        super(handle);
    }
    
    public TileEntity getTileEntity(BlockPosition pos) {
        try {
            return new TileEntity(getTileEntityMethod.invoke(handle, pos.getHandle()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object getHandle(org.bukkit.World world) {
        try {
            return getHandleMethod.invoke(world);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
