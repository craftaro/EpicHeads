package com.songoda.epicheads.head;


import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import com.songoda.epicheads.utils.settings.Setting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Head {

    private final int id;
    private final String name;
    private final String URL;
    private final String pack;
    private final byte staffPicked;

    private final Category category;

    public Head(int id, String name, String URL, Category category, String pack, byte staffPicked) {
        this.id = id;
        this.name = name;
        this.URL = URL;
        this.category = category;
        this.pack = pack;
        this.staffPicked = staffPicked;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPack() { return pack; }

    public String getURL() {
        return URL;
    }

    public Category getCategory() {
        return category;
    }

    public byte getStaffPicked() {
        return staffPicked;
    }

    public ItemStack asItemStack() {
        return asItemStack(false, false);
    }

    public ItemStack asItemStack(boolean favorite) { return asItemStack(favorite, false); }

    public ItemStack asItemStack(boolean favorite, boolean free) {
        EpicHeads plugin = EpicHeads.getInstance();
        ItemStack item = Methods.addTexture(new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                        ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3), this.URL);

        double cost = Setting.HEAD_COST.getDouble();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.formatText((favorite ? "&6⭐ " : "") + "&9" + name));
        List<String> lore = new ArrayList<>();
        if (this.staffPicked == 1)
            lore.add(Methods.formatText(plugin.getLocale().getMessage("general.head.staffpicked")));
        lore.add(Methods.formatText(plugin.getLocale().getMessage("general.head.id", this.id)));
        if (!free)
            lore.add(plugin.getLocale().getMessage("general.head.cost", cost));

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String toString() {
        return "Head:{"
                + "Id:\"" + id + "\","
                + "Name:\"" + name + "\","
                + "URL:\"" + URL + "\","
                + "Category:\"" + category.getName() + "\","
                + "Pack:\"" + pack + "\","
                + "StaffPicked:\"" + staffPicked + "\""
                + "}";
    }
}
