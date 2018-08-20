package net.sothatsit.heads.volatilecode.reflection.craftbukkit;

import java.lang.reflect.Field;

import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagCompound;

public class CraftMetaSkullSub1 extends ReflectObject {
    
    public static Class<?> CraftMetaSkullSub1Class;
    public static Field tagField;
    public static Field metaField;
    
    static {
        CraftMetaSkullSub1Class = ReflectionUtils.getCraftBukkitClass("inventory.CraftMetaSkull$1");
        Checks.ensureNonNull(CraftMetaSkullSub1Class, "CraftMetaSkullSub1Class");
        
        try {
            tagField = CraftMetaSkullSub1Class.getDeclaredField("val$tag");
            tagField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        
        try {
            metaField = CraftMetaSkullSub1Class.getDeclaredField("this$0");
            metaField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
    
    public CraftMetaSkullSub1(Object handle) {
        super(handle);
    }
    
    public NBTTagCompound getTag() {
        try {
            return new NBTTagCompound(tagField.get(getHandle()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public CraftMetaSkull getMeta() {
        try {
            return new CraftMetaSkull(metaField.get(getHandle()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
