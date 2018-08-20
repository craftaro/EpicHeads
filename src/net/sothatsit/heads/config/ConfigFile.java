package net.sothatsit.heads.config;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.menu.ui.item.Item;
import net.sothatsit.heads.util.Checks;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ConfigFile {

    private final String name;

    public ConfigFile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract ConfigurationSection getConfig();
    
    public abstract void save();
    
    public abstract void reload();
    
    public abstract ConfigurationSection getDefaults();

    public abstract void copyDefaults();

    public boolean getOrCopyDefault(String key, boolean defaultValue, AtomicBoolean requiresSave) {
        Checks.ensureNonNull(key, "key");
        Checks.ensureNonNull(requiresSave, "requiresSave");

        ConfigurationSection config = getConfig();

        if(!config.isSet(key) || !config.isBoolean(key))
            return replaceInvalid(key, defaultValue, requiresSave);

        return config.getBoolean(key);
    }

    public int getOrCopyDefault(String key, int defaultValue, AtomicBoolean requiresSave) {
        Checks.ensureNonNull(key, "key");
        Checks.ensureNonNull(requiresSave, "requiresSave");

        ConfigurationSection config = getConfig();

        if(!config.isSet(key) || !config.isInt(key))
            return replaceInvalid(key, defaultValue, requiresSave);

        return config.getInt(key);
    }

    public double getOrCopyDefault(String key, double defaultValue, AtomicBoolean requiresSave) {
        Checks.ensureNonNull(key, "key");
        Checks.ensureNonNull(requiresSave, "requiresSave");

        ConfigurationSection config = getConfig();

        if(!config.isSet(key) || (!config.isDouble(key) && !config.isInt(key) && !config.isLong(key)))
            return replaceInvalid(key, defaultValue, requiresSave);

        return config.getDouble(key);
    }

    public String getOrCopyDefault(String key, String defaultValue, AtomicBoolean requiresSave) {
        Checks.ensureNonNull(key, "key");
        Checks.ensureNonNull(requiresSave, "requiresSave");

        ConfigurationSection config = getConfig();

        if(!config.isSet(key) || !config.isString(key))
            return replaceInvalid(key, defaultValue, requiresSave);

        return config.getString(key);
    }

    public Item getOrCopyDefault(String key, Item defaultValue, AtomicBoolean requiresSave) {
        Checks.ensureNonNull(key, "key");
        Checks.ensureNonNull(defaultValue, "defaultValue");
        Checks.ensureNonNull(requiresSave, "requiresSave");

        ConfigurationSection config = getConfig();

        if(!config.isSet(key) || !config.isConfigurationSection(key))
            return replaceInvalid(key, defaultValue, requiresSave);

        Item item = Item.load(name, config.getConfigurationSection(key), requiresSave);

        if(item == null)
            return replaceInvalid(key, defaultValue, requiresSave);

        return item;
    }

    private Item replaceInvalid(String key, Item replacement, AtomicBoolean requiresSave) {
        Heads.warning("\"" + key + "\" not set or invalid in " + getName() + ", replacing with " + replacement);

        removeInvalid(key, requiresSave);
        replacement.save(getConfig(), key);

        requiresSave.set(true);

        return replacement;
    }

    private <T> T replaceInvalid(String key, T replacement, AtomicBoolean requiresSave) {
        Heads.warning("\"" + key + "\" not set or invalid in " + getName() + ", replacing with " + replacement);

        removeInvalid(key, requiresSave);
        getConfig().set(key, replacement);

        requiresSave.set(true);

        return replacement;
    }

    private void removeInvalid(String key, AtomicBoolean requiresSave) {
        ConfigurationSection config = getConfig();

        if(!config.isSet(key))
            return;

        String toKey = key + "-invalid";

        int counter = 2;
        while(config.isSet(toKey)) {
            toKey = key + "-invalid-" + (counter++);
        }

        config.set(toKey, config.get(key));
        config.set(key, null);

        requiresSave.set(true);
    }
    
}
