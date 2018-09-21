package com.songoda.epicheads.volatilecode.reflection.nms;

import com.songoda.epicheads.volatilecode.reflection.ReflectObject;
import com.songoda.epicheads.volatilecode.reflection.ReflectionUtils;

import java.lang.reflect.Method;

public class MinecraftServer extends ReflectObject {
    
    public static Class<?> MinecraftServerClass;
    public static Method getServerMethod;
    public static Method getUserCacheMethod;
    
    static {
        MinecraftServerClass = ReflectionUtils.getNMSClass("MinecraftServer");
        
        getServerMethod = ReflectionUtils.getMethod(MinecraftServerClass, "getServer", true, MinecraftServerClass);
        getUserCacheMethod = ReflectionUtils.getMethod(MinecraftServerClass, "getUserCache", UserCache.UserCacheClass);
    }
    
    public MinecraftServer(Object handle) {
        super(handle);
    }
    
    public UserCache getUserCache() {
        try {
            return new UserCache(getUserCacheMethod.invoke(handle));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static MinecraftServer getServer() {
        try {
            return new MinecraftServer(getServerMethod.invoke(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
