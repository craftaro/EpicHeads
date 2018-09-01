package nl.marido.heads.volatilecode.reflection.nms.nbt;

import java.lang.reflect.Constructor;

import nl.marido.heads.volatilecode.reflection.ReflectObject;
import nl.marido.heads.volatilecode.reflection.ReflectionUtils;

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
