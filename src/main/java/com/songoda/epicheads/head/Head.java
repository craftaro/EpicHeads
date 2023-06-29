package com.songoda.epicheads.head;

import com.craftaro.core.utils.ItemUtils;
import com.craftaro.core.utils.TextUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.settings.Settings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Head {
    private int id;
    private String name = null;
    private String url = null;
    private String pack = null;
    private byte staffPicked = 0;
    private final boolean local;

    private Category category;

    public Head(String name, String url, Category category, boolean local, String pack, byte staffPicked) {
        this.name = name;
        this.url = url;
        this.category = category;
        this.pack = pack;
        this.staffPicked = staffPicked;
        this.local = local;
    }

    public Head(int id, String name, String url, Category category, boolean local, String pack, byte staffPicked) {
        this.id = id;
        this.name = name;
        this.url = url;
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
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public String getPack() {
        return this.pack;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated Use {@link #setUrl(String)} instead.
     */
    @Deprecated
    public void setURL(String url) {
        setUrl(url);
    }

    /**
     * @deprecated Use {@link #getUrl()} instead.
     */
    @Deprecated
    public String getURL() {
        return getUrl();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        if (this.url == null) {
            return "d23eaefbd581159384274cdbbd576ced82eb72423f2ea887124f9ed33a6872c";
        }
        return this.url;
    }

    public Category getCategory() {
        return this.category;
    }

    public byte getStaffPicked() {
        return this.staffPicked;
    }

    public boolean isLocal() {
        return this.local;
    }

    public ItemStack asItemStack() {
        return asItemStack(false, false);
    }

    public ItemStack asItemStack(boolean favorite) {
        return asItemStack(favorite, false);
    }

    public ItemStack asItemStack(boolean favorite, boolean free) {
        ItemStack item = ItemUtils.getCustomHead(this.url);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getHeadItemName(favorite));
            meta.setLore(getHeadItemLore(free));
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getHeadItemName(boolean favorite) {
        return TextUtils.formatText((favorite ? "&6⭐ " : "") + "&9" + this.name);
    }

    public List<String> getHeadItemLore(boolean free) {
        EpicHeads plugin = EpicHeads.getInstance();
        double cost = Settings.HEAD_COST.getDouble();
        List<String> lore = new ArrayList<>();
        if (this.staffPicked == 1) {
            lore.add(plugin.getLocale().getMessage("general.head.staffpicked").getMessage());
        }
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Head head = (Head) o;
        return this.id == head.id && this.local == head.local;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.local);
    }

    @Override
    public String toString() {
        return "Head{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", URL='" + this.url + '\'' +
                ", pack='" + this.pack + '\'' +
                ", staffPicked=" + this.staffPicked +
                ", local=" + this.local +
                ", category=" + this.category +
                '}';
    }
}
