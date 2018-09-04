package nl.marido.deluxeheads.volatilecode.reflection.craftbukkit;

import java.lang.reflect.Field;

import com.mojang.authlib.GameProfile;

import nl.marido.deluxeheads.util.Checks;
import nl.marido.deluxeheads.volatilecode.reflection.ReflectObject;
import nl.marido.deluxeheads.volatilecode.reflection.ReflectionUtils;

public class CraftMetaSkull extends ReflectObject {
    
    public static Class<?> CraftMetaSkullClass;
    public static Field profileField;
    
    static {
        CraftMetaSkullClass = ReflectionUtils.getCraftBukkitClass("inventory.CraftMetaSkull");
        Checks.ensureNonNull(CraftMetaSkullClass, "CraftMetaSkullClass");

        try {
            profileField = CraftMetaSkullClass.getDeclaredField("profile");
            profileField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
    public CraftMetaSkull(Object handle) {
        super(handle);
    }
    
    public GameProfile getProfile() {
        try {
            return (GameProfile) profileField.get(getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
