package com.songoda.epicheads.head;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.settings.Settings;
import com.songoda.epicheads.utils.Methods;
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

    public String getPack() {
        return pack;
    }

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

    public ItemStack asItemStack(boolean favorite) {
        return asItemStack(favorite, false);
    }

    public ItemStack asItemStack(boolean favorite, boolean free) {
        ItemStack item = Methods.addTexture(CompatibleMaterial.PLAYER_HEAD.getItem(), this.URL);

        ItemMeta meta = item.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(getHeadItemName(favorite));
            meta.setLore(getHeadItemLore(free));
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getHeadItemName(boolean favorite) {
        return Methods.formatText((favorite ? "&6⭐ " : "") + "&9" + name);
    }

    public List<String> getHeadItemLore(boolean free) {
        EpicHeads plugin = EpicHeads.getInstance();
        double cost = Settings.HEAD_COST.getDouble();
        List<String> lore = new ArrayList<>();
        if (this.staffPicked == 1)
            lore.add(plugin.getLocale().getMessage("general.head.staffpicked").getMessage());
        lore.add(plugin.getLocale().getMessage("general.head.id")
                .processPlaceholder("id", this.id).getMessage());
        if (!free)
            lore.add(plugin.getLocale().getMessage("general.head.cost")
                    .processPlaceholder("cost", cost).getMessage());
        return lore;
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
