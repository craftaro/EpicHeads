package net.sothatsit.heads.volatilecode.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.bukkit.Bukkit;

public final class ReflectionUtils {
    
    public static String getServerVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
    
    public static Class<?> getNMSClass(String ClassName) {
        String className = "net.minecraft.server." + getServerVersion() + "." + ClassName;
        
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Class<?> getCraftBukkitClass(String ClassPackageName) {
        String className = "org.bukkit.craftbukkit." + getServerVersion() + "." + ClassPackageName;
        
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
        outer: for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            Class<?>[] para = c.getParameterTypes();
            
            if (para.length != params.length) {
                continue;
            }
            
            for (int i = 0; i < para.length; i++) {
                if (!para[i].equals(params[i])) {
                    continue outer;
                }
            }
            
            return c;
        }
        reportNotFound("Could not find constructor in class " + clazz);
        return null;
    }
    
    public static Method getMethod(Class<?> clazz, String name) {
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                m.setAccessible(true);
                return m;
            }
        }
        reportNotFound("Could not find method " + name + " in class " + clazz);
        return null;
    }
    
    public static Method getMethod(Class<?> clazz, Class<?> returnType, Class<?>... params) {
        return getMethod(clazz, null, false, returnType, params);
    }
    
    public static Method getMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... params) {
        return getMethod(clazz, name, false, returnType, params);
    }
    
    public static Method getMethod(Class<?> clazz, boolean staticMethod, Class<?> returnType, Class<?>... params) {
        return getMethod(clazz, null, staticMethod, returnType, params);
    }
    
    public static Method getMethod(Class<?> clazz, String name, boolean staticMethod, Class<?> returnType, Class<?>... params) {
        outer: for (Method m : clazz.getDeclaredMethods()) {
            if (name != null && !m.getName().equals(name)) {
                continue;
            }
            
            if (staticMethod != Modifier.isStatic(m.getModifiers())) {
                continue;
            }
            
            if (!m.getReturnType().equals(returnType)) {
                continue;
            }
            
            Class<?>[] para = m.getParameterTypes();
            
            if (para.length != params.length) {
                continue;
            }
            
            for (int i = 0; i < para.length; i++) {
                if (!para[i].equals(params[i])) {
                    continue outer;
                }
            }
            
            return m;
        }
        reportNotFound("Could not find method " + name + " in class " + clazz);
        return null;
    }

    public static void reportNotFound(String message) {
        new Exception(message).printStackTrace();
    }
}
