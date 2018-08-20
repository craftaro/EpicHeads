package net.sothatsit.heads.volatilecode.reflection.craftbukkit;

import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.nms.ItemStack;

public class CraftItemStack extends ReflectObject {
    
    public static Class<?> CraftItemStackClass;
    public static Method asBukkitCopyMethod;
    public static Method asNMSCopyMethod;
    
    static {
        CraftItemStackClass = ReflectionUtils.getCraftBukkitClass("inventory.CraftItemStack");
        
        asBukkitCopyMethod = ReflectionUtils.getMethod(CraftItemStackClass, "asBukkitCopy", true, org.bukkit.inventory.ItemStack.class,
                ItemStack.ItemStackClass);
        
        asNMSCopyMethod = ReflectionUtils.getMethod(CraftItemStackClass, "asNMSCopy", true, ItemStack.ItemStackClass,
                org.bukkit.inventory.ItemStack.class);
    }
    
    public CraftItemStack(Object handle) {
        super(handle);
    }
    
    public static org.bukkit.inventory.ItemStack asBukkitCopy(ItemStack item) {
        try {
            return (org.bukkit.inventory.ItemStack) asBukkitCopyMethod.invoke(null, item.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static ItemStack asNMSCopy(org.bukkit.inventory.ItemStack item) {
        try {
            return new ItemStack(asNMSCopyMethod.invoke(null, item));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
