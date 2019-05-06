package com.songoda.epicheads.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.songoda.epicheads.EpicHeads;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Iterator;
import java.util.UUID;

public class Methods {

    public static ItemStack addTexture(ItemStack item, String headURL) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(headURL.getBytes()), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"http://textures.minecraft.net/texture/%s\"}}}", new Object[]{headURL}).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        item.setItemMeta(meta);
        return item;
    }

    public static String getDecodedTexture(ItemStack item) {
        try {
            SkullMeta localSkullMeta = (SkullMeta) item.getItemMeta();
            Field localField = localSkullMeta.getClass().getDeclaredField("profile");
            localField.setAccessible(true);
            GameProfile localGameProfile = (GameProfile) localField.get(localSkullMeta);
            Iterator localIterator = localGameProfile.getProperties().get("textures").iterator();
            if (localIterator.hasNext()) {
                Property localProperty = (Property) localIterator.next();
                byte[] decoded = Base64.getDecoder().decode(localProperty.getValue());
                return StringUtils.substringBetween(new String(decoded), "texture/", "\"");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack getGlass() {
        EpicHeads instance = EpicHeads.getInstance();
        return Methods.getGlass(instance.getConfig().getBoolean("Interfaces.Replace Glass Type 1 With Rainbow Glass"), instance.getConfig().getInt("Interfaces.Glass Type 1"));
    }

    public static ItemStack getBackgroundGlass(boolean type) {
        EpicHeads instance = EpicHeads.getInstance();
        if (type)
            return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 2"));
        else
            return getGlass(false, instance.getConfig().getInt("Interfaces.Glass Type 3"));
    }

    private static ItemStack getGlass(Boolean rainbow, int type) {
        int randomNum = 1 + (int) (Math.random() * 6);
        ItemStack glass;
        if (rainbow) {
            glass = new ItemStack(EpicHeads.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ?
                    Material.LEGACY_STAINED_GLASS_PANE :  Material.valueOf("STAINED_GLASS_PANE"), 1, (short) randomNum);
        } else {
            glass = new ItemStack(EpicHeads.getInstance().isServerVersionAtLeast(ServerVersion.V1_13) ?
                    Material.LEGACY_STAINED_GLASS_PANE :  Material.valueOf("STAINED_GLASS_PANE"), 1, (short) type);
        }
        ItemMeta glassmeta = glass.getItemMeta();
        glassmeta.setDisplayName("§l");
        glass.setItemMeta(glassmeta);
        return glass;
    }


    public static String formatText(String text) {
        if (text == null || text.equals(""))
            return "";
        return formatText(text, false);
    }

    public static String formatText(String text, boolean cap) {
        if (text == null || text.equals(""))
            return "";
        if (cap)
            text = text.substring(0, 1).toUpperCase() + text.substring(1);
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatTitle(String text) {
        if (text == null || text.equals(""))
            return "";
        if (!EpicHeads.getInstance().isServerVersionAtLeast(ServerVersion.V1_9)) {
            if (text.length() > 31)
                text = text.substring(0, 29) + "...";
        }
        text = formatText(text);
        return text;
    }
}
