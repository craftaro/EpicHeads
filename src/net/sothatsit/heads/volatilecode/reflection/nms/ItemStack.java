package net.sothatsit.heads.volatilecode.reflection.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sothatsit.heads.volatilecode.reflection.ReflectObject;
import net.sothatsit.heads.volatilecode.reflection.ReflectionUtils;
import net.sothatsit.heads.volatilecode.reflection.Version;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagCompound;

public class ItemStack extends ReflectObject {
    
    public static Class<?> ItemStackClass;
    public static Constructor<?> ItemStackConstructor;
    public static Method getTagMethod;
    public static Method setTagMethod;
    
    static {
        ItemStackClass = ReflectionUtils.getNMSClass("ItemStack");
        
        if(Version.isBelow(Version.v1_13)) {
            ItemStackConstructor = ReflectionUtils.getConstructor(ItemStackClass, Item.ItemClass, int.class, int.class);
        } else {
            ItemStackConstructor = ReflectionUtils.getConstructor(ItemStackClass, ReflectionUtils.getNMSClass("IMaterial"), int.class);
        }
        
        getTagMethod = ReflectionUtils.getMethod(ItemStackClass, "getTag", NBTTagCompound.NBTTagCompoundClass);
        setTagMethod = ReflectionUtils.getMethod(ItemStackClass, "setTag", void.class, NBTTagCompound.NBTTagCompoundClass);
    }
    
    public ItemStack(Object handle) {
        super(handle);
    }

    public ItemStack(Item item, int amount) {
        super(newInstance(item, amount, 0));
    }

    public ItemStack(Item item, int amount, int data) {
        super(newInstance(item, amount, data));
    }
    
    public NBTTagCompound getTag() {
        try {
            return new NBTTagCompound(getTagMethod.invoke(handle));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setTag(NBTTagCompound compound) {
        try {
            setTagMethod.invoke(handle, compound.getHandle());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Object newInstance(Item item, int amount, int data) {
        try {
            if(Version.isBelow(Version.v1_13)) {
                return ItemStackConstructor.newInstance(item.getHandle(), amount, data);
            } else {
                return ItemStackConstructor.newInstance(item.getHandle(), amount);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
