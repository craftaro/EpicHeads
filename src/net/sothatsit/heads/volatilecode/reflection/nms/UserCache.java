package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mojang.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

public class UserCache extends ReflectObject {
    
    public static final Class<?> UserCacheClass;
    public static final List<Field> mapFields;
    public static final Method addProfileMethod;
    
    static {
        UserCacheClass = ReflectionUtils.getNMSClass("UserCache");

        if(UserCacheClass == null)
            throw new IllegalStateException("Unable to find UserCache class");

        mapFields = new ArrayList<>();
        for(Field field : UserCacheClass.getDeclaredFields()) {
            if(!Map.class.isAssignableFrom(field.getType()))
                continue;

            field.setAccessible(true);

            mapFields.add(field);
        }

        addProfileMethod = ReflectionUtils.getMethod(UserCacheClass, void.class, GameProfile.class);
    }
    
    public UserCache(Object handle) {
        super(handle);
    }
    
    public GameProfile getCachedProfile(String name) {
        try {
            name = name.toLowerCase(Locale.ROOT);

            for(Field field : mapFields) {
                Map<?, ?> map = (Map<?, ?>) field.get(handle);

                if(map == null)
                    continue;

                Object value = map.get(name);

                if(value == null || !UserCacheEntry.UserCacheEntryClass.isAssignableFrom(value.getClass()))
                    continue;

                UserCacheEntry entry = new UserCacheEntry(value);
                GameProfile profile = entry.getProfile();

                if(profile == null)
                    continue;

                return profile;
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void addProfile(GameProfile profile) {
        try {
            addProfileMethod.invoke(handle, profile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
