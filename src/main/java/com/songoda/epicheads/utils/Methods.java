package com.songoda.epicheads.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.songoda.epicheads.EpicHeads;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Methods {

    private static Class<?> clazzCraftPlayer;
    private static Method methodGetProfile;

    public static ItemStack addTexture(ItemStack item, String headURL) {
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if (headURL == null) return item;

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

    public static String getEncodedTexture(ItemStack item) {
        try {
            SkullMeta localSkullMeta = (SkullMeta) item.getItemMeta();
            Field localField = localSkullMeta.getClass().getDeclaredField("profile");
            localField.setAccessible(true);
            GameProfile profile = (GameProfile) localField.get(localSkullMeta);
            Iterator<Property> iterator = profile.getProperties().get("textures").iterator();

            if (!iterator.hasNext()) return null;

            Property property = iterator.next();
            return property.getValue();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEncodedTexture(Player player) {
        try {
            if (clazzCraftPlayer == null) {
                String ver = Bukkit.getServer().getClass().getPackage().getName().substring(23);
                clazzCraftPlayer = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer");
                methodGetProfile = clazzCraftPlayer.getMethod("getProfile");
            }
            Object craftPlayer = clazzCraftPlayer.cast(player);

            Iterator<Property> iterator = ((GameProfile) methodGetProfile.invoke(craftPlayer)).getProperties().get("textures").iterator();

            if (!iterator.hasNext()) return null;

            Property property = iterator.next();
            return property.getValue();
        } catch (ClassNotFoundException
                | NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDecodedTexture(String encoded) {
        return StringUtils.substringBetween(new String(Base64.getDecoder().decode(encoded)), "texture/", "\"");
    }

    public static ItemStack createToken(int amount) {
        ItemStack itemStack = new ItemStack(Material.valueOf(SettingsManager.Setting.ITEM_TOKEN_TYPE.getString()));

        if (itemStack.getType() == (EpicHeads.getInstance().isServerVersionAtLeast(ServerVersion.V1_13)
                ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"))) {
            itemStack = EpicHeads.getInstance().getHeadManager().getHeads().stream()
                    .filter(head -> head.getId() == SettingsManager.Setting.ITEM_TOKEN_ID.getInt())
                    .findFirst().get().asItemStack();
        }
        itemStack.setAmount(amount);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(formatText(SettingsManager.Setting.ITEM_TOKEN_NAME.getString()));
        List<String> lore = new ArrayList<>();
        for (String line : SettingsManager.Setting.ITEM_TOKEN_LORE.getStringList())
            lore.add(formatText(line));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
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
