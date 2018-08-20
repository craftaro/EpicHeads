package net.sothatsit.heads.volatilecode;

import net.sothatsit.heads.menu.ui.item.Item;
import net.sothatsit.heads.volatilecode.reflection.Version;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Methods to deal with items on different Spigot versions.
 */
public class Items {

    public static boolean isSkull(ItemStack item) {
        if(item == null)
            return false;

        if(Version.isBelow(Version.v1_13))
            return item.getType().name().equals("SKULL_ITEM") && item.getDurability() == 3;

        return item.getType() == Material.PLAYER_HEAD;
    }

    public static Item createSkull() {
        if(Version.isBelow(Version.v1_13))
            return Item.create(Material.valueOf("SKULL_ITEM"), (byte) 3);

        return Item.create(Material.PLAYER_HEAD);
    }

    public static Item createRedStainedClay() {
        if(Version.isBelow(Version.v1_13))
            return Item.create(Material.valueOf("STAINED_CLAY"), (byte) 14);

        return Item.create(Material.RED_TERRACOTTA);
    }

    public static Item createGreenStainedClay() {
        if(Version.isBelow(Version.v1_13))
            return Item.create(Material.valueOf("STAINED_CLAY"), (byte) 5);

        return Item.create(Material.GREEN_TERRACOTTA);
    }

    public static Item createRedStainedGlassPane() {
        if(Version.isBelow(Version.v1_13))
            return Item.create(Material.valueOf("STAINED_GLASS_PANE"), (byte) 14);

        return Item.create(Material.RED_STAINED_GLASS_PANE);
    }

    public static Item createBlackStainedGlassPane() {
        if(Version.isBelow(Version.v1_13))
            return Item.create(Material.valueOf("STAINED_GLASS_PANE"), (byte) 15);

        return Item.create(Material.BLACK_STAINED_GLASS_PANE);
    }

    public static Item createEmptyMap() {
        if(Version.isBelow(Version.v1_13))
            return Item.create(Material.valueOf("EMPTY_MAP"));

        return Item.create(Material.MAP);
    }
}
