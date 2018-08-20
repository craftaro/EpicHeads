package net.sothatsit.heads.volatilecode;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.volatilecode.reflection.Version;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CraftItemStack;
import net.sothatsit.heads.volatilecode.reflection.nms.ItemStack;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagCompound;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemNBT {

    public static org.bukkit.inventory.ItemStack addGlow(org.bukkit.inventory.ItemStack itemstack) {
        itemstack = itemstack.clone();

        if(Version.getVersion().higherThan(Version.v1_10)) {
            itemstack.addUnsafeEnchantment(Enchantment.LURE, 1);

            ItemMeta meta = itemstack.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemstack.setItemMeta(meta);

            return itemstack;
        } else {
            ItemStack item = CraftItemStack.asNMSCopy(itemstack);

            NBTTagCompound tag = item.getTag();

            if (tag.isNull())
                tag = new NBTTagCompound();

            tag.set("ench", new NBTTagList());

            item.setTag(tag);

            return CraftItemStack.asBukkitCopy(item);
        }
    }

    public static String getTextureProperty(org.bukkit.inventory.ItemStack item) {
        return getTextureProperty(CraftItemStack.asNMSCopy(item));
    }
    
    public static String getTextureProperty(ItemStack item) {
        NBTTagCompound tag = item.getTag();
        
        if (tag == null || tag.getHandle() == null) {
            return null;
        }
        
        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        
        if (skullOwner == null || skullOwner.getHandle() == null) {
            return null;
        }
        
        NBTTagCompound properties = skullOwner.getCompound("Properties");
        
        if (properties == null || properties.getHandle() == null) {
            return null;
        }
        
        NBTTagList textures = properties.getList("textures", 10);
        
        if (textures == null || textures.getHandle() == null || textures.size() == 0) {
            return null;
        }
        
        return textures.get(0).getString("Value");
    }

    private static ItemStack createNMSSkull() {
        if(Version.isBelow(Version.v1_13))
            return new ItemStack(net.sothatsit.heads.volatilecode.reflection.nms.Items.getItem("SKULL"), 1, 3);

        return new ItemStack(net.sothatsit.heads.volatilecode.reflection.nms.Items.getItem("PLAYER_HEAD"), 1);
    }

    public static org.bukkit.inventory.ItemStack createHead(CacheHead head, String name) {
        if(name == null) {
            name = ChatColor.GRAY + head.getName();
        }

        ItemStack nmsItemstack = createNMSSkull();
        NBTTagCompound tag = nmsItemstack.getTag();

        if (tag.getHandle() == null) {
            tag = new NBTTagCompound();

            nmsItemstack.setTag(tag);
        }

        tag.set("display", createDisplayTag(name, new String[] {ChatColor.DARK_GRAY + head.getCategory()}));

        return CraftItemStack.asBukkitCopy(applyNBT(head, nmsItemstack));
    }
    
    public static org.bukkit.inventory.ItemStack createHead(GameProfile profile, String name) {
        ItemStack nmsItemstack = createNMSSkull();
        NBTTagCompound tag = nmsItemstack.getTag();
        
        if (tag.getHandle() == null) {
            tag = new NBTTagCompound();

            nmsItemstack.setTag(tag);
        }

        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        skullOwner.setString("Id", UUID.randomUUID().toString());
        skullOwner.setString("Name", "SpigotHeadPlugin");
        
        NBTTagCompound properties = skullOwner.getCompound("Properties");
        NBTTagList textures = new NBTTagList();
        
        for (Property property : profile.getProperties().get("textures")) {
            NBTTagCompound value = new NBTTagCompound();
            value.setString("Value", property.getValue());

            if(property.hasSignature()) {
                value.setString("Signature", property.getSignature());
            }
            
            textures.add(value);
        }
        
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);
        tag.set("SkullOwner", skullOwner);

        tag.set("display", createDisplayTag(name, new String[0]));
        
        nmsItemstack.setTag(tag);

        return CraftItemStack.asBukkitCopy(nmsItemstack);
    }

    public static NBTTagCompound createDisplayTag(String name, String[] lore) {
        NBTTagCompound display = new NBTTagCompound();

        if(Version.isBelow(Version.v1_13)) {
            display.setString("Name", name);

            NBTTagList list = new NBTTagList();
            for(String line : lore) {
                list.add(new NBTTagString(line));
            }

            display.set("Lore", list);
        } else {
            display.setString("Name", ComponentSerializer.toString(TextComponent.fromLegacyText(name)));

            NBTTagList list = new NBTTagList();
            for(String line : lore) {
                list.add(new NBTTagString(ComponentSerializer.toString(TextComponent.fromLegacyText(line))));
            }

            display.set("Lore", list);
        }

        return display;
    }
    
    public static org.bukkit.inventory.ItemStack applyHead(CacheHead head, org.bukkit.inventory.ItemStack item) {
        if(!Items.isSkull(item))
            return item;
        
        ItemStack itemstack = CraftItemStack.asNMSCopy(item);
        
        return CraftItemStack.asBukkitCopy(applyNBT(head, itemstack));
    }

    private static ItemStack copy(ItemStack itemstack) {
        return CraftItemStack.asNMSCopy(CraftItemStack.asBukkitCopy(itemstack));
    }

    public static ItemStack applyNBT(CacheHead head, ItemStack itemstack) {
        itemstack = copy(itemstack);
        NBTTagCompound tag = itemstack.getTag();
        
        if (tag.getHandle() == null) {
            tag = new NBTTagCompound();
            
            itemstack.setTag(tag);
        }

        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        skullOwner.setString("Id", UUID.randomUUID().toString());
        skullOwner.setString("Name", "SpigotHeadPlugin");

        NBTTagCompound properties = skullOwner.getCompound("Properties");
        NBTTagList textures = new NBTTagList();

        NBTTagCompound value = new NBTTagCompound();
        value.setString("Value", head.getTexture());

        if(Bukkit.getPluginManager().getPlugin("SkinsRestorer") == null) {
            value.setString("Signature", "");
        }

        textures.add(value);

        properties.set("textures", textures);
        skullOwner.set("Properties", properties);
        tag.set("SkullOwner", skullOwner);

        NBTTagCompound headInfo = new NBTTagCompound();

        headInfo.setString("id", Integer.toString(head.getId()));
        headInfo.setString("name", head.getName());
        headInfo.setString("category", head.getCategory());
        headInfo.setString("texture", head.getTexture());
        headInfo.setString("cost", Double.toString(head.getCost()));
        headInfo.setString("permission", head.getPermission());

        tag.set("SpigotHeadPlugin", headInfo);

        itemstack.setTag(tag);
        
        return itemstack;
    }

}
