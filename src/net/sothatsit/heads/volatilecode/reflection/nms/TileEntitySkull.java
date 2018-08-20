package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.Executor;

import com.google.common.base.Predicate;

import com.mojang.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class TileEntitySkull extends ReflectObject {
    
    public static Class<?> TileEntitySkullClass;
    public static Method resolveTextureMethod;
    public static Method getGameProfileMethod;
    public static Field executorField;
    
    static {
        TileEntitySkullClass = ReflectionUtils.getNMSClass("TileEntitySkull");
        getGameProfileMethod = ReflectionUtils.getMethod(TileEntitySkullClass, "getGameProfile");
        
        for (Method m : TileEntitySkullClass.getDeclaredMethods()) {
            Class<?>[] params = m.getParameterTypes();

            if(!Modifier.isStatic(m.getModifiers()))
                continue;

            if(params.length != 2 && params.length != 3)
                continue;

            if(!params[0].equals(GameProfile.class) || !params[1].equals(Predicate.class))
                continue;

            if(params.length == 3 && !params[2].equals(boolean.class))
                continue;

            resolveTextureMethod = m;
            resolveTextureMethod.setAccessible(true);
            break;
        }


        
        try {
            executorField = TileEntitySkullClass.getDeclaredField("executor");
            executorField.setAccessible(true);
            
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(executorField, executorField.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            executorField = null;
        }
    }
    
    public TileEntitySkull(Object handle) {
        super(handle);
    }
    
    public GameProfile getGameProfile() {
        try {
            return (GameProfile) getGameProfileMethod.invoke(handle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resolveTexture(String name, Predicate<GameProfile> callback) {
        GameProfile existingProfile = MinecraftServer.getServer().getUserCache().getCachedProfile(name);

        if (existingProfile == null) {
            existingProfile = new GameProfile(null, name);
        }

        TileEntitySkull.resolveTexture(existingProfile, callback);
    }
    
    public static void resolveTexture(GameProfile profile, Predicate<GameProfile> callback) {
        try {
            if(resolveTextureMethod.getParameterCount() == 2) {
                resolveTextureMethod.invoke(null, profile, callback);
            } else {
                resolveTextureMethod.invoke(null, profile, callback, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static Executor getExecutor() {
        try {
            return (Executor) executorField.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void setExecutor(Executor executor) {
        try {
            executorField.set(null, executor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
