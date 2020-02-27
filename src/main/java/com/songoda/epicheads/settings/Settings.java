package com.songoda.epicheads.settings;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Static config node accessors
 */
public class Settings {

    static final Config config = EpicHeads.getInstance().getCoreConfig();

    public static final ConfigSetting AUTOSAVE = new ConfigSetting(config, "Main.Auto Save Interval In Seconds", 15,
            "The amount of time in between saving to file.",
            "This is purely a safety function to prevent against unplanned crashes or",
            "restarts. With that said it is advised to keep this enabled.",
            "If however you enjoy living on the edge, feel free to turn it off.");

    public static final ConfigSetting DISCORD = new ConfigSetting(config, "Main.Show Discord Button", true,
            "This is the discord button displayed in the main GUI",
            "Clicking this button will bring you to a discord where you can",
            "add or remove heads to the global library this plugin uses.",
            "AS well as get updates on future releases and features.");

    public static final ConfigSetting FREE_IN_CREATIVE = new ConfigSetting(config, "Main.Heads Free In Creative Mode", false,
            "Enabling this will make it so that a player can get all heads",
            "for free as long as they are in the creative game mode.");

    public static final ConfigSetting DROP_MOB_HEADS = new ConfigSetting(config, "Main.Drop Mob Heads", true,
            "Should heads drop after a monster is killed?");

    public static final ConfigSetting DROP_PLAYER_HEADS = new ConfigSetting(config, "Main.Drop Player Heads", true,
            "Should a players drop their head on death?");

    public static final ConfigSetting DROP_CHANCE = new ConfigSetting(config, "Main.Head Drop Chance", "25%",
            "When a player or monster is killed what should be",
            "the chance that their head drops?");

    public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(config, "Economy.Economy", "Vault",
            "Which economy plugin should be used?");

    public static final ConfigSetting HEAD_COST = new ConfigSetting(config, "Economy.Head Cost", 24.99,
            "The cost the of the head. If you wan't to use PlayerPoints",
            "or item tokens you need to use whole numbers.");

    public static final ConfigSetting ITEM_TOKEN_TYPE = new ConfigSetting(config, "Economy.Item.Type", "PLAYER_HEAD",
            "Which item material type should be used?",
            "You can use any of the materials from the following link:",
            "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");

    public static final ConfigSetting ITEM_TOKEN_ID = new ConfigSetting(config, "Economy.Item.Head ID", 14395,
            "If a player head is used as the token which head ID should be used?",
            "This can be any head from the global database.");

    public static final ConfigSetting ITEM_TOKEN_NAME = new ConfigSetting(config, "Economy.Item.Name", "&6Player Head Token",
            "What should the token be named?");

    public static final ConfigSetting ITEM_TOKEN_LORE = new ConfigSetting(config, "Economy.Item.Lore", Arrays.asList("&8Use in /Heads!"),
            "What should the tokens lore be?");

    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static final ConfigSetting GLASS_TYPE_1 = new ConfigSetting(config, "Interfaces.Glass Type 1", "GRAY_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_2 = new ConfigSetting(config, "Interfaces.Glass Type 2", "BLUE_STAINED_GLASS_PANE");
    public static final ConfigSetting GLASS_TYPE_3 = new ConfigSetting(config, "Interfaces.Glass Type 3", "LIGHT_BLUE_STAINED_GLASS_PANE");

    /**
     * In order to set dynamic economy comment correctly, this needs to be
     * called after EconomyManager load
     */
    public static void setupConfig() {
        config.load();
        config.setAutoremove(true).setAutosave(true);

        // convert glass pane settings
        int color;
        if ((color = GLASS_TYPE_1.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_1.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_2.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_2.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }
        if ((color = GLASS_TYPE_3.getInt(-1)) != -1) {
            config.set(GLASS_TYPE_3.getKey(), CompatibleMaterial.getGlassPaneColor(color).name());
        }

        config.setDefault("Economy.Economy",
                EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName());
        config.setComment("Economy.Economy", null,
                "Which economy plugin should be used?",
                "Supported plugins you have installed: \"" + EconomyManager.getManager().getRegisteredPlugins().stream().filter(p -> !p.equals("EpicHeads")).collect(Collectors.joining("\", \"")) + "\", \"Item\".");

        // convert economy settings
        if (config.getBoolean("Economy.Use Vault Economy") && EconomyManager.getManager().isEnabled("Vault")) {
            config.set("Economy.Economy", "Vault");
        } else if (config.getBoolean("Economy.Use Reserve Economy") && EconomyManager.getManager().isEnabled("Reserve")) {
            config.set("Economy.Economy", "Reserve");
        } else if (config.getBoolean("Economy.Use Player Points Economy") && EconomyManager.getManager().isEnabled("PlayerPoints")) {
            config.set("Economy.Economy", "PlayerPoints");
        } else if (config.getBoolean("Economy.Use Item Economy")) {
            config.set("Economy.Economy", "Item");
        }

        config.saveChanges();
    }
}