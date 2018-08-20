package net.sothatsit.heads.volatilecode.reflection.nms.nbt;

import java.lang.reflect.Method;
import java.util.Set;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class NBTTagCompound extends ReflectObject {
    
    public static Class<?> NBTTagCompoundClass;
    public static Method getKeysMethod;
    public static Method getCompoundMethod;
    public static Method getMethod;
    public static Method getListMethod;
    public static Method getStringMethod;
    public static Method setStringMethod;
    public static Method setMethod;
    public static Method hasKeyMethod;
    public static Method hasKeyOfTypeMethod;
    
    static {
        NBTTagCompoundClass = ReflectionUtils.getNMSClass("NBTTagCompound");

        getKeysMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, Set.class);
        getCompoundMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "getCompound", NBTTagCompoundClass, String.class);
        getMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "get", NBTBase.NBTBaseClass, String.class);
        getListMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "getList", NBTTagList.NBTTagListClass, String.class, int.class);
        getStringMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "getString", String.class, String.class);
        setStringMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "setString", void.class, String.class, String.class);
        setMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "set", void.class, String.class, NBTBase.NBTBaseClass);
        hasKeyMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "hasKey", boolean.class, String.class);
        hasKeyOfTypeMethod = ReflectionUtils.getMethod(NBTTagCompoundClass, "hasKeyOfType", boolean.class, String.class, int.class);
    }
    
    public NBTTagCompound(Object handle) {
        super(handle);
    }
    
    public NBTTagCompound() {
        super(newInstance());
    }
    
    public NBTTagCompound getCompound(String key) {
        try {
            return new NBTTagCompound(getCompoundMethod.invoke(handle, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public NBTTagList getList(String key, int type) {
        try {
            return new NBTTagList(getListMethod.invoke(handle, key, type));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NBTTagList getList(String key) {
        return new NBTTagList(get(key).getHandle());
    }

    @SuppressWarnings("unchecked")
    public Set<String> getKeys() {
        try {
            return (Set<String>) getKeysMethod.invoke(handle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NBTBase get(String key) {
        try {
            return new NBTBase(getMethod.invoke(handle, key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getString(String key) {
        try {
            return (String) getStringMethod.invoke(handle, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setString(String key, String value) {
        try {
            setStringMethod.invoke(handle, key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void set(String key, ReflectObject value) {
        try {
            setMethod.invoke(handle, key, value.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean hasKey(String key) {
        try {
            return (boolean) hasKeyMethod.invoke(handle, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasKeyOfType(String key, int type) {
        try {
            return (boolean) hasKeyOfTypeMethod.invoke(handle, key, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object newInstance() {
        try {
            return NBTTagCompoundClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
