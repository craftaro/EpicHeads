package com.songoda.epicheads.head;


import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.SettingsManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Head {

    private final int id;
    private final String name;
    private final String URL;
    private final byte staffPicked;

    private final Tag tag;

    public Head(int id, String name, String URL, Tag tag, byte staffPicked) {
        this.id = id;
        this.name = name;
        this.URL = URL;
        this.tag = tag;
        this.staffPicked = staffPicked;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return URL;
    }

    public Tag getTag() {
        return tag;
    }

    public byte getStaffPicked() {
        return staffPicked;
    }

    public ItemStack asItemStack() {
        return asItemStack(false, false);
    }

    public ItemStack asItemStack(boolean favorite) { return asItemStack(favorite, false); }

    public ItemStack asItemStack(boolean favorite, boolean includeCost) {
        ItemStack item = Methods.addTexture(new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3),
                this.URL);

        double cost = SettingsManager.Setting.PRICE.getDouble();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.formatText((favorite ? "&6⭐ " : "") + "&9" + name));
        List<String> lore = new ArrayList<>();
        if (this.staffPicked == 1)
            lore.add(Methods.formatText(EpicHeads.getInstance().getLocale().getMessage("general.head.staffpicked")));
        lore.add(Methods.formatText(EpicHeads.getInstance().getLocale().getMessage("general.head.id", this.id)));
        if (includeCost)
            lore.add(EpicHeads.getInstance().getLocale().getMessage("general.head.cost", cost));

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
                + "Tags:\"" + tag.getName() + "\","
                + "StaffPicked:\"" + staffPicked + "\""
                + "}";
    }
}
