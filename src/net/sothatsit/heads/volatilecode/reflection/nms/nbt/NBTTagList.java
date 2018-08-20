package net.sothatsit.heads.volatilecode.reflection.nms.nbt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.Version;

public class NBTTagList extends ReflectObject {
    
    public static Class<?> NBTTagListClass;
    public static Method addMethod;
    public static Method sizeMethod;
    public static Method getMethod;
    public static Field typeField;
    
    static {
        NBTTagListClass = ReflectionUtils.getNMSClass("NBTTagList");

        if(Version.isBelow(Version.v1_13)) {
            addMethod = ReflectionUtils.getMethod(NBTTagListClass, "add", void.class, NBTBase.NBTBaseClass);
            getMethod = ReflectionUtils.getMethod(NBTTagListClass, "get", NBTTagCompound.NBTTagCompoundClass, int.class);
        } else {
            addMethod = ReflectionUtils.getMethod(NBTTagListClass, "add", boolean.class, NBTBase.NBTBaseClass);
            getMethod = ReflectionUtils.getMethod(NBTTagListClass, "get", NBTBase.NBTBaseClass, int.class);
        }

        sizeMethod = ReflectionUtils.getMethod(NBTTagListClass, "size", int.class);

        for(Field field : NBTTagListClass.getDeclaredFields()) {
            if(field.getType().equals(byte.class)) {
                typeField = field;
                typeField.setAccessible(true);
                break;
            }
        }

        if(typeField == null) {
            ReflectionUtils.reportNotFound("Could not find byte type field in NBTTagList");
        }
    }
    
    public NBTTagList(Object handle) {
        super(handle);
    }
    
    public NBTTagList() {
        super(newInstance());
    }
    
    public void add(ReflectObject value) {
        try {
            addMethod.invoke(handle, value.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public int size() {
        try {
            return (int) sizeMethod.invoke(handle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public NBTTagCompound get(int index) {
        try {
            return new NBTTagCompound(getMethod.invoke(handle, index));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getType() {
        try {
            return (int) (Byte) typeField.get(handle);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object newInstance() {
        try {
            return NBTTagListClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
