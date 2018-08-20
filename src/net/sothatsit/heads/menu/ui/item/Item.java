package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import net.sothatsit.heads.volatilecode.ItemNBT;
import net.sothatsit.heads.volatilecode.reflection.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class Item {

    private final Material type;
    private final int amount;
    private final short damage;

    private final String name;
    private final String[] lore;

    private final boolean enchanted;

    private Item(Material type) {
        this(type, 1, (short) 0, null, null, false);
    }

    private Item(Material type, int amount, short damage, String name, String[] lore, boolean enchanted) {
        Checks.ensureNonNull(type, "type");
        Checks.ensureTrue(amount > 0, "amount must be greater than 0");
        Checks.ensureTrue(damage >= 0, "damage must be greater than or equal to 0");

        if(lore != null) {
            Checks.ensureArrayNonNull(lore, "lore");
        }

        this.type = type;
        this.amount = amount;
        this.damage = damage;
        this.name = name;
        this.lore = (lore == null || lore.length == 0 ? null : lore);
        this.enchanted = enchanted;
    }

    public Item amount(int amount) {
        return new Item(type, amount, damage, name, lore, enchanted);
    }

    public Item damage(short damage) {
        return new Item(type, amount, damage, name, lore, enchanted);
    }

    public Item name(String name) {
        return new Item(type, amount, damage, name, lore, enchanted);
    }

    public Item lore(String... lore) {
        return new Item(type, amount, damage, name, lore, enchanted);
    }

    public Item enchanted(boolean enchanted) {
        return new Item(type, amount, damage, name, lore, enchanted);
    }

    public Button buildButton(Placeholder... placeholders) {
        return new Button(build(placeholders));
    }

    public Button buildButton(Callable<MenuResponse> callable, Placeholder... placeholders) {
        return new Button(build(placeholders), callable);
    }

    public ItemStack build(Placeholder... placeholders) {
        return build(null, placeholders);
    }

    public ItemStack build(Function<String, Boolean> loreFilter, Placeholder... placeholders) {
        Checks.ensureNonNull(placeholders, "placeholders");

        ItemStack item = new ItemStack(type, amount, damage);

        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        if(name != null) {
            String displayName = ChatColor.translateAlternateColorCodes('&', name);

            displayName = Placeholder.applyAll(displayName, placeholders);

            meta.setDisplayName(displayName);
        }

        if(lore != null) {
            String[] itemLore = Placeholder.colourAll(lore);

            itemLore = Placeholder.filterAndApplyAll(itemLore, loreFilter, placeholders);

            meta.setLore(Arrays.asList(itemLore));
        }

        item.setItemMeta(meta);

        if(enchanted) {
            item = ItemNBT.addGlow(item);
        }

        return item;
    }

    public void save(ConfigurationSection section, String key) {
        section.set(key, null);
        save(section.createSection(key));
    }

    public void save(ConfigurationSection section) {
        section.set("type", getTypeName(type));

        if(amount != 1) {
            section.set("amount", amount);
        }

        if(damage != 0) {
            section.set("damage", damage);
        }

        if(name != null) {
            section.set("name", name);
        }

        if(lore != null) {
            section.set("lore", Arrays.asList(lore));
        }

        if(enchanted) {
            section.set("enchanted", true);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Item))
            return false;

        Item other = (Item) obj;

        return other.type == type
                && other.amount == amount
                && Objects.equals(other.name, name)
                && (other.lore == null ? lore == null : Arrays.equals(other.lore, lore))
                && other.enchanted == enchanted;
    }

    @Override
    public String toString() {
        Stringify.Builder properties = Stringify.builder();
        {
            properties.entry("type", getTypeName(type));

            if(amount != 1) {
                properties.entry("amount", amount);
            }

            if(name != null) {
                properties.entry("name", name);
            }

            if(Version.isBelow(Version.v1_13)) {
                if(damage != 0) {
                    properties.entry("data", damage);
                }
            } else {
                if(damage != 0) {
                    properties.entry("damage", damage);
                }
            }

            if(lore != null) {
                properties.entry("lore", lore);
            }

            if(enchanted) {
                properties.entry("enchanted", true);
            }
        }
        return properties.toString();
    }

    public static Item create(Material type) {
        return new Item(type);
    }

    /**
     * @deprecated data is only supported pre-1.13
     */
    @Deprecated
    public static Item create(Material type, byte data) {
        return new Item(type, 1, data, null, null, false);
    }

    public static Item create(ItemStack itemStack) {
        Item item = create(itemStack.getType())
                .amount(itemStack.getAmount())
                .damage(itemStack.getDurability());

        ItemMeta meta = itemStack.getItemMeta();

        if(meta == null)
            return item;

        if(meta.hasDisplayName()) {
            String name = meta.getDisplayName().replace(ChatColor.COLOR_CHAR, '&');

            item = item.name(name);
        }

        if(meta.hasLore()) {
            List<String> rawLore = meta.getLore();
            String[] lore = new String[rawLore.size()];

            for(int index = 0; index < lore.length; ++index) {
                lore[index] = rawLore.get(index).replace(ChatColor.COLOR_CHAR, '&');
            }

            item = item.lore(lore);
        }

        if(meta.hasEnchants()) {
            item = item.enchanted(true);
        }

        return item;
    }

    private static void updateLegacyTypes(String filename, ConfigurationSection section, AtomicBoolean shouldSave) {
        if(!section.isSet("type"))
            return;

        if(Version.isBelow(Version.v1_13) && section.isSet("data")) {
            section.set("damage", section.get("data"));
            section.set("data", null);
            shouldSave.set(true);
        }

        String typeName = section.getString("type");
        String typeData = section.getString("damage", null);
        Material type = Material.matchMaterial(typeName);
        if(type != null && !section.isInt("type"))
            return;

        if(section.isInt("type")) {
            int typeId = section.getInt("type");
            String convertedType = Heads.getLegacyIDs().fromId(typeId);

            if(convertedType == null) {
                Heads.warning("Invalid type of item " + section.getCurrentPath() + ", " +
                              "unknown type id " + typeId);
                return;
            }

            if(Version.isBelow(Version.v1_13)) {
                type = Material.matchMaterial(convertedType);
            } else {
                type = null;
            }

            section.set("type", convertedType.toLowerCase());
        }

        boolean legacy = false;
        if(type == null && !Version.isBelow(Version.v1_13)) {
            type = Material.valueOf("LEGACY_" + section.getString("type").toUpperCase().replace(' ', '_'));
            legacy = true;
        }

        if(type == null) {
            Heads.warning("Invalid type of item " + section.getCurrentPath() + ", could not find type " + typeName);
            return;
        }

        if(legacy && !Version.isBelow(Version.v1_13)) {
            Material legacyType = type;
            int data = section.getInt("damage");
            byte byteData = (byte) (data >= 0 && data < 16 ? data : 0);

            // Get a type to begin with, to check if the data is a damage value
            Material withoutData = fromLegacyType(legacyType, (byte) 0);
            type = fromLegacyType(legacyType, byteData);
            if(type == null) {
                Heads.warning("Invalid legacy type of item " + section.getCurrentPath() + ": " +
                              "Could not convert " + legacyType + ":" + data + " to non-legacy format");
                return;
            }

            if(withoutData != type) {
                section.set("damage", null);
            }
        }

        section.set("type", type.name().toLowerCase());

        String from = typeName + (typeData != null ? ":" + typeData : "");
        String to = type.name().toLowerCase() + (section.isSet("damage") ? ":" + section.get("damage") : "");
        Heads.info("1.13 Update - " + from + " converted to " + to +
                   " for " + filename + " -> " + section.getCurrentPath());

        shouldSave.set(true);
    }

    public static Item load(String filename, ConfigurationSection section, AtomicBoolean shouldSave) {
        // Convert from legacy type ids to type names
        updateLegacyTypes(filename, section, shouldSave);

        if (!section.isSet("type") || !section.isString("type")) {
            Heads.warning("Invalid type of item " + section.getCurrentPath() + " in " + filename + ", " +
                          "expected a type name");
            return null;
        }

        String typeName = section.getString("type");
        Material type = Material.matchMaterial(typeName);

        if(type == null) {
            Heads.warning("Invalid type of item " + section.getCurrentPath() + ", " +
                          "unknown material for type name " + typeName);
            return null;
        }

        short damage = (short) section.getInt("damage", 0);

        if(damage < 0) {
            Heads.warning("Invalid damage of item " + section.getCurrentPath() + ", " +
                          "damage must be at least 0");
            return null;
        }

        int amount = section.getInt("amount", 1);

        if(amount < 1) {
            Heads.warning("Invalid amount of item " + section.getCurrentPath() + ", " +
                          "amount must be at least 1");
            return null;
        }

        String name = section.getString("name", null);
        String[] lore = section.getStringList("lore").toArray(new String[0]);
        boolean enchanted = section.getBoolean("enchanted", false);

        return new Item(type, amount, damage, name, lore, enchanted);
    }

    public static String getTypeName(Material type) {
        return type.name().toLowerCase();
    }

    public static Material getType(String typeName) {
        Material type = Material.matchMaterial(typeName);
        if(type != null || Version.isBelow(Version.v1_13))
            return type;

        return Material.matchMaterial(typeName, true);
    }

    public static Material fromLegacyType(Material legacyType, byte data) {
        return Bukkit.getUnsafe().fromLegacy(new MaterialData(legacyType, data));
    }
}
