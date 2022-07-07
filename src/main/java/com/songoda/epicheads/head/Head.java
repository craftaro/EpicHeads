package com.songoda.epicheads.head;

import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.utils.ItemUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.settings.Settings;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Head {

    private final int id;
    private String name = null;
    private String URL = null;
    private String pack = null;
    private byte staffPicked = 0;
    private final boolean local;

    private Category category;

    public Head(int id, String name, String URL, Category category, boolean local, String pack, byte staffPicked) {
        this.id = id;
        this.name = name;
        this.URL = URL;
        this.category = category;
        this.pack = pack;
        this.staffPicked = staffPicked;
        this.local = local;
    }

    public Head(int id, boolean local) {
        this.id = id;
        this.local = local;
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
        if (URL == null)
            return "d23eaefbd581159384274cdbbd576ced82eb72423f2ea887124f9ed33a6872c";
        return URL;
    }

    public Category getCategory() {
        return category;
    }

    public byte getStaffPicked() {
        return staffPicked;
    }

    public boolean isLocal() {
        return local;
    }

    public ItemStack asItemStack() {
        return asItemStack(false, false);
    }

    public ItemStack asItemStack(boolean favorite) {
        return asItemStack(favorite, false);
    }

    public ItemStack asItemStack(boolean favorite, boolean free) {
        ItemStack item = ItemUtils.getCustomHead(this.URL);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
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
        if (!free) {
            String fcost = Settings.ECONOMY_PLUGIN.getString().equalsIgnoreCase("item")
                    ? cost + " " + Settings.ITEM_TOKEN_TYPE.getString()
                    : /* EconomyManager.formatEconomy(cost) */ String.valueOf(cost);  // FIXME: EconomyManager#formatEconomy etc only work in some languages (. vs ,) and only for the currency symbol $
            lore.add(plugin.getLocale().getMessage("general.head.cost")
                    .processPlaceholder("cost", fcost).getMessage());
        }
        return lore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Head head = (Head) o;
        return id == head.id &&
                local == head.local;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, local);
    }

    @Override
    public String toString() {
        return "Head{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", URL='" + URL + '\'' +
                ", pack='" + pack + '\'' +
                ", staffPicked=" + staffPicked +
                ", local=" + local +
                ", category=" + category +
                '}';
    }
}
