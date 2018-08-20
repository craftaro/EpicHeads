package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Constructor;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class BlockPosition extends ReflectObject {
    
    public static Class<?> BlockPositionClass;
    public static Constructor<?> BlockPositionConstructor;
    
    static {
        BlockPositionClass = ReflectionUtils.getNMSClass("BlockPosition");
        
        BlockPositionConstructor = ReflectionUtils.getConstructor(BlockPositionClass, int.class, int.class, int.class);
    }
    
    public BlockPosition(Object handle) {
        super(handle);
    }
    
    public BlockPosition(int x, int y, int z) {
        super(newInstance(x, y, z));
    }
    
    private static Object newInstance(int x, int y, int z) {
        try {
            return BlockPositionConstructor.newInstance(x, y, z);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
