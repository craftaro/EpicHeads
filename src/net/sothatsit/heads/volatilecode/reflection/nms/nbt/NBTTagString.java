package net.sothatsit.heads.volatilecode.reflection.nms.nbt;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

import java.lang.reflect.Constructor;

public class NBTTagString extends ReflectObject {

    public static Class<?> NBTTagStringClass;
    public static Constructor<?> stringConstructor;

    static {
        NBTTagStringClass = ReflectionUtils.getNMSClass("NBTTagString");

        stringConstructor = ReflectionUtils.getConstructor(NBTTagStringClass, String.class);
    }

    public NBTTagString(Object handle) {
        super(handle);
    }

    public NBTTagString() {
        super(newInstance());
    }

    public NBTTagString(String value) {
        super(newInstance(value));
    }
    
    private static Object newInstance() {
        try {
            return NBTTagStringClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object newInstance(String value) {
        try {
            return stringConstructor.newInstance(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
