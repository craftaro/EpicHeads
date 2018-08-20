package net.sothatsit.heads.config.oldmenu;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.ConfigFile;

import net.sothatsit.heads.util.Clock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class MenuConfig {
    
    private final ConfigurationSection defaults;
    private final ConfigFile configFile;
    private final Map<String, Menu> menus;
    private final Map<String, Menu> defaultMenus;
    
    public MenuConfig(ConfigFile configFile) {
        this.menus = new HashMap<>();
        this.defaultMenus = new HashMap<>();

        this.configFile = configFile;
        this.defaults = loadDefaults();

        reload();
    }

    public Menu getMenu(String name) {
        Menu menu = menus.get(name.toLowerCase());

        return (menu != null ? menu : defaultMenus.get(name.toLowerCase()));
    }
    
    public void reload() {
        Clock timer = Clock.start();
        
        configFile.copyDefaults();
        configFile.reload();

        String filename = configFile.getName();
        ConfigurationSection config = configFile.getConfig();
        AtomicBoolean shouldSave = new AtomicBoolean(false);

        menus.clear();

        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key)) {
                Heads.warning("Unknown use of value " + key + " in " + filename);
                continue;
            }

            ConfigurationSection menuSection = config.getConfigurationSection(key);

            Menu defaultMenu = defaultMenus.get(key.toLowerCase());
            Menu menu = Menu.loadMenu(filename, menuSection, shouldSave, defaultMenu);
            
            menus.put(key.toLowerCase(), menu);
        }
        
        for (String key : defaultMenus.keySet()) {
            if(menus.containsKey(key))
                continue;

            config.set(key, defaults.getConfigurationSection(key));

            Heads.warning(key + " was missing in " + filename + ", creating it");
            shouldSave.set(true);
        }
        
        if (shouldSave.get()) {
            configFile.save();
        }
        
        Heads.info("Loaded Menu Config with " + menus.size() + " Menus " + timer);
    }

    private ConfigurationSection loadDefaults() {
        String filename = configFile.getName();
        ConfigurationSection config = configFile.getDefaults();
        AtomicBoolean shouldSave = new AtomicBoolean(false);

        defaultMenus.clear();

        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key))
                continue;

            ConfigurationSection menuSection = config.getConfigurationSection(key);

            defaultMenus.put(key.toLowerCase(), Menu.loadMenu(filename, menuSection, shouldSave));
        }

        return config;
    }

}
