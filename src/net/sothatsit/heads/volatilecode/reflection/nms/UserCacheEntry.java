package net.sothatsit.heads.volatilecode.reflection.nms;

import com.mojang.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;

import java.lang.reflect.Field;

public class UserCacheEntry extends ReflectObject {

    public static final Class<?> UserCacheEntryClass;
    public static final Field profileField;

    static {
        UserCacheEntryClass = ReflectionUtils.getNMSClass("UserCache$UserCacheEntry");

        if(UserCacheEntryClass == null)
            throw new IllegalStateException("Unable to find UserCache$UserCacheEntry class");

        Field gameProfileField = null;
        for(Field field : UserCacheEntryClass.getDeclaredFields()) {
            if(!GameProfile.class.isAssignableFrom(field.getType()))
                continue;

            field.setAccessible(true);
            gameProfileField = field;
            break;
        }
        profileField = gameProfileField;
    }

    public UserCacheEntry(Object handle) {
        super(handle);
    }
    
    public GameProfile getProfile() {
        try {
            return (GameProfile) profileField.get(handle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
