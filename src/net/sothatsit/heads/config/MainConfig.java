package net.sothatsit.heads.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sothatsit.heads.Heads;

import net.sothatsit.heads.menu.ui.item.Item;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Clock;
import net.sothatsit.heads.volatilecode.Items;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

public class MainConfig {
    
    private final ConfigFile configFile;

    private boolean economyEnabled;
    private double defaultHeadCost;
    private boolean vaultEcoEnabled;
    private boolean itemEcoEnabled;
    private Item itemEcoItem;
    private boolean playerPointsEcoEnabled;

    private boolean headNamesEnabled;
    private boolean useBlockStore;
    private boolean useCacheNames;
    private String defaultHeadName;

    private boolean hideNoPermCategories;
    private boolean freeInCreative;
    private boolean checkForUpdates;

    private Map<String, Double> categoryCosts;
    
    private String headLabel;
    private String[] headAliases;
    private String headDescription;

    private String reloadLabel;
    private String addLabel;
    private String handLabel;
    private String getLabel;
    private String giveLabel;
    private String randomLabel;
    private String removeLabel;
    private String renameLabel;
    private String costLabel;
    private String categoryCostLabel;
    private String itemEcoLabel;
    private String idLabel;
    private String searchLabel;
    private String helpLabel;
    
    public MainConfig() {
        this.configFile = Heads.getVersionedConfig("config.yml");
        
        reload();
    }

    public void reload() {
        Clock timer = Clock.start();

        configFile.copyDefaults();
        configFile.reload();
        
        ConfigurationSection config = configFile.getConfig();

        AtomicBoolean shouldSave = new AtomicBoolean(false);

        loadCommandInfo(config, shouldSave);
        loadCategoryCosts(config, shouldSave);

        if(config.isSet("hat-mode") && config.isBoolean("hat-mode") && config.getBoolean("hat-mode")) {
            Heads.severe("--------------------------------------------------");
            Heads.severe("Until further notice, hat mode is no longer supported");
            Heads.severe("in Heads past version 1.10.0, please downgrade or");
            Heads.severe("switch the plugin out of hat-mode in your config.yml");
            Heads.severe("--------------------------------------------------");

            Bukkit.getScheduler().scheduleSyncDelayedTask(Heads.getInstance(), () -> {
                Heads.severe("--------------------------------------------------");
                Heads.severe("Until further notice, hat mode is no longer supported");
                Heads.severe("in Heads past version 1.10.0, please downgrade or");
                Heads.severe("switch the plugin out of hat-mode in your config.yml");
                Heads.severe("--------------------------------------------------");

                Bukkit.getPluginManager().disablePlugin(Heads.getInstance());
            });
        }

        economyEnabled  = loadBoolean(config, "economy.enabled", false, shouldSave);
        defaultHeadCost = loadDouble(config, "economy.default-head-cost", 0, shouldSave);
        vaultEcoEnabled = loadBoolean(config, "economy.vault-eco.enabled", true, shouldSave);
        itemEcoEnabled  = loadBoolean(config, "economy.item-eco.enabled", false, shouldSave);

        Item defaultItemEcoItem = Items.createSkull()
                .name("&6Player Head Token")
                .lore("&8Use in /heads!");

        itemEcoItem = loadItem(config, "economy.item-eco.item", defaultItemEcoItem, shouldSave);

        playerPointsEcoEnabled = loadBoolean(config, "economy.player-points-eco.enabled", false, shouldSave);

        headNamesEnabled = loadBoolean(config, "breaking-head-names.enabled", true, shouldSave);
        useBlockStore    = loadBoolean(config, "breaking-head-names.attempt-hook-blockstore", true, shouldSave);
        useCacheNames    = loadBoolean(config, "breaking-head-names.similar-heads-in-cache", true, shouldSave);
        defaultHeadName  = loadString(config, "breaking-head-names.default-name", "Decoration Head", shouldSave);

        hideNoPermCategories = loadBoolean(config, "hide-no-perm-categories", true, shouldSave);
        freeInCreative       = loadBoolean(config, "free-in-creative", false, shouldSave);
        checkForUpdates      = loadBoolean(config, "check-for-updates", true, shouldSave);

        if (defaultHeadCost < 0) {
            Heads.info("\"economy.default-head-cost\" cannot be less than 0 in config.yml, defaulting to 0");
            defaultHeadCost = 0;
        }

        if (shouldSave.get()) {
            configFile.save();
        }
        
        Heads.info("Loaded Main Config " + timer);
    }

    private void loadCommandInfo(ConfigurationSection config, AtomicBoolean shouldSave) {
        reloadLabel       = loadString(config, "commands.heads.sub-commands.reload", "reload", shouldSave);
        addLabel          = loadString(config, "commands.heads.sub-commands.add", "add", shouldSave);
        handLabel         = loadString(config, "commands.heads.sub-commands.hand", "hand", shouldSave);
        getLabel          = loadString(config, "commands.heads.sub-commands.get", "get", shouldSave);
        giveLabel         = loadString(config, "commands.heads.sub-commands.give", "give", shouldSave);
        randomLabel       = loadString(config, "commands.heads.sub-commands.random", "random", shouldSave);
        removeLabel       = loadString(config, "commands.heads.sub-commands.remove", "remove", shouldSave);
        renameLabel       = loadString(config, "commands.heads.sub-commands.rename", "rename", shouldSave);
        costLabel         = loadString(config, "commands.heads.sub-commands.cost", "cost", shouldSave);
        categoryCostLabel = loadString(config, "commands.heads.sub-commands.category-cost", "categorycost", shouldSave);
        itemEcoLabel      = loadString(config, "commands.heads.sub-commands.item-eco", "itemeco", shouldSave);
        idLabel           = loadString(config, "commands.heads.sub-commands.id", "id", shouldSave);
        searchLabel       = loadString(config, "commands.heads.sub-commands.search", "search", shouldSave);
        helpLabel         = loadString(config, "commands.heads.sub-commands.help", "help", shouldSave);

        headLabel         = loadString(config, "commands.heads.label", "heads", shouldSave);
        headDescription   = loadString(config, "commands.heads.description", "Get a cool head", shouldSave);
        headAliases       = loadStringArray(config, "commands.heads.aliases", new String[] {"head"}, shouldSave);
    }

    private void loadCategoryCosts(ConfigurationSection config, AtomicBoolean shouldSave) {
        categoryCosts = new HashMap<>();

        if(!config.isSet("economy.categories") || !config.isConfigurationSection("economy.categories"))
            return;

        ConfigurationSection categories = config.getConfigurationSection("economy.categories");

        for(String key : categories.getKeys(false)) {
            double cost = categories.getDouble(key, -1);

            if(cost < 0)
                continue;

            categoryCosts.put(key.toLowerCase(), cost);
        }
    }
    
    private String loadString(ConfigurationSection config, String key, String defaultVal, AtomicBoolean shouldSave) {
        if (config.isSet(key) && config.isString(key) && !config.getString(key).isEmpty())
            return config.getString(key);

        Heads.warning("\"" + key + "\" not set or invalid in config.yml, resetting to \"" + defaultVal + "\"");

        config.set(key, defaultVal);
        shouldSave.set(true);

        return defaultVal;
    }

    private String[] loadStringArray(ConfigurationSection config, String key, String[] defaultVal, AtomicBoolean shouldSave) {
        if(config.isSet(key) && config.isList(key))
            return config.getStringList(key).toArray(new String[0]);

        Heads.warning("\"" + key + "\" not set or invalid in config.yml, resetting to " + Arrays.toString(defaultVal));

        config.set(key, Arrays.asList(defaultVal));
        shouldSave.set(true);

        return defaultVal;
    }

    private boolean loadBoolean(ConfigurationSection config, String key, boolean defaultVal, AtomicBoolean shouldSave) {
        if(config.isSet(key) && config.isBoolean(key))
            return config.getBoolean(key);

        Heads.warning("\"" + key + "\" not set or invalid in config.yml, resetting to " + defaultVal);

        config.set(key, defaultVal);
        shouldSave.set(true);

        return defaultVal;
    }

    private double loadDouble(ConfigurationSection config, String key, double defaultVal, AtomicBoolean shouldSave) {
        if(config.isSet(key) && (config.isInt(key) || config.isDouble(key)))
            return config.getDouble(key);

        Heads.warning("\"" + key + "\" not set or invalid in config.yml, resetting to " + defaultVal);

        config.set(key, defaultVal);
        shouldSave.set(true);

        return defaultVal;
    }

    private Item loadItem(ConfigurationSection config, String key, Item defaultItem, AtomicBoolean shouldSave) {
        if(config.isSet(key) && config.isConfigurationSection(key)) {
            Item item = Item.load("config.yml", config.getConfigurationSection(key), shouldSave);

            if(item != null)
                return item;
        }

        Heads.warning(key + " not set or invalid in config.yml, resetting to " + defaultItem);

        config.set(key, null);
        defaultItem.save(config.createSection(key));
        shouldSave.set(true);

        return defaultItem;
    }

    private String getPlainCategoryName(String category) {
        return category.toLowerCase().replace(" ", "");
    }

    public boolean hasCategoryCost(String category) {
        return categoryCosts.containsKey(getPlainCategoryName(category));
    }

    public double getCategoryCost(String category) {
        return categoryCosts.getOrDefault(getPlainCategoryName(category), defaultHeadCost);
    }

    public void setCategoryCost(String category, double cost) {
        categoryCosts.put(getPlainCategoryName(category), cost);

        saveCategoryCosts();
    }

    public void removeCategoryCost(String category) {
        categoryCosts.remove(getPlainCategoryName(category));

        saveCategoryCosts();
    }

    private void saveCategoryCosts() {
        Clock timer = Clock.start();

        ConfigurationSection config = configFile.getConfig();

        config.set("economy.categories", null);

        if(categoryCosts.size() > 0) {
            ConfigurationSection section = config.createSection("economy.categories");

            for(Map.Entry<String, Double> entry : categoryCosts.entrySet()) {
                section.set(entry.getKey(), entry.getValue());
            }
        }

        configFile.save();

        Heads.info("Saved Main Config " + timer);
    }

    public void setItemEcoItem(Item item) {
        Checks.ensureNonNull(item, "item");

        this.itemEcoItem = item;

        saveItemEcoItem();
    }

    private void saveItemEcoItem() {
        Clock timer = Clock.start();

        ConfigurationSection config = this.configFile.getConfig();

        config.set("economy.item-eco.item", null);
        itemEcoItem.save(config.createSection("economy.item-eco.item"));

        configFile.save();

        Heads.info("Saved Main Config " + timer);
    }

    public boolean isEconomyEnabled() {
        return economyEnabled;
    }

    public double getDefaultHeadCost() {
        return defaultHeadCost;
    }

    public boolean isVaultEconomyEnabled() {
        return vaultEcoEnabled;
    }

    public boolean isItemEconomyEnabled() {
        return itemEcoEnabled;
    }

    public Item getItemEconomyItem() {
        return itemEcoItem;
    }

    public boolean isPlayerPointsEconomyEnabled() {
        return playerPointsEcoEnabled;
    }

    public boolean isHeadNamesEnabled() {
        return headNamesEnabled;
    }

    public boolean shouldUseBlockStore() {
        return useBlockStore;
    }

    public boolean shouldUseCacheNames() {
        return useCacheNames;
    }

    public String getDefaultHeadName() {
        return defaultHeadName;
    }

    public boolean shouldHideNoPermCategories() {
        return hideNoPermCategories;
    }

    public boolean isFreeInCreative() {
        return freeInCreative;
    }

    public boolean shouldCheckForUpdates() {
        return checkForUpdates;
    }

    public String getHeadCommand() {
        return headLabel;
    }
    
    public String[] getHeadAliases() {
        return headAliases;
    }
    
    public String getHeadDescription() {
        return headDescription;
    }

    public String getReloadCommand() {
        return reloadLabel;
    }

    public String getAddCommand() {
        return addLabel;
    }
    
    public String getHandCommand() {
        return handLabel;
    }

    public String getGetCommand() {
        return getLabel;
    }
    
    public String getGiveCommand() {
        return giveLabel;
    }
    
    public String getRandomCommand() {
        return randomLabel;
    }
    
    public String getRemoveCommand() {
        return removeLabel;
    }
    
    public String getRenameCommand() {
        return renameLabel;
    }
    
    public String getCostCommand() {
        return costLabel;
    }

    public String getCategoryCostCommand() {
        return categoryCostLabel;
    }

    public String getItemEcoCommand() {
        return itemEcoLabel;
    }
    
    public String getIdCommand() {
        return idLabel;
    }

    public String getSearchCommand() {
        return searchLabel;
    }

    public String getHelpCommand() {
        return helpLabel;
    }
}
