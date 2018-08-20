package net.sothatsit.heads;

import net.sothatsit.heads.cache.*;
import net.sothatsit.heads.cache.legacy.CacheFileConverter;
import net.sothatsit.heads.command.HeadsCommand;
import net.sothatsit.heads.command.RuntimeCommand;
import net.sothatsit.heads.config.FileConfigFile;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.cache.legacy.LegacyCacheConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.config.lang.LangConfig;
import net.sothatsit.heads.config.menu.Menus;
import net.sothatsit.heads.config.oldmenu.MenuConfig;
import net.sothatsit.heads.economy.*;
import net.sothatsit.heads.menu.ui.InventoryMenu;
import net.sothatsit.heads.oldmenu.ClickInventory;
import net.sothatsit.heads.util.Clock;
import net.sothatsit.heads.volatilecode.injection.ProtocolHackFixer;
import net.sothatsit.heads.volatilecode.reflection.Version;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CommandMap;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CraftServer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

public class Heads extends JavaPlugin implements Listener {

    private static Heads instance;
    private CacheFile cache;
    private MenuConfig oldMenuConfig;
    private Menus menus;
    private MainConfig mainConfig;
    private LangConfig langConfig;
    private Economy economy;
    private LegacyIDs legacyIDs;
    private boolean commandsRegistered = false;
    private boolean blockStoreAvailable = false;
    
    @Override
    public void onEnable() {
        if(Version.isBelow(Version.v1_8)) {
            System.err.println("------------------------------------------------------------");
            System.err.println("    Heads no longer supports versions below MineCraft 1.8   ");
            System.err.println("       Please switch to Heads version 1.15.1 or before      ");
            System.err.println("------------------------------------------------------------");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        instance = this;

        Clock timer = Clock.start();

        loadCache();

        try {
            legacyIDs = LegacyIDs.readResource("legacy-ids.txt");
        } catch(IOException exception) {
            legacyIDs = LegacyIDs.EMPTY;
            severe("Unable to load legacy IDs to perform conversion from older Spigot versions");
            exception.printStackTrace();
        }

        this.menus = new Menus();
        this.menus.reload();

        this.oldMenuConfig = new MenuConfig(getVersionedConfig("menus.yml"));
        this.langConfig = new LangConfig();
        this.mainConfig = new MainConfig();
        this.economy = hookEconomy();

        ProtocolHackFixer.fix();

        registerCommands();
        tryHookBlockStore();

        new HeadNamer().registerEvents();

        Bukkit.getPluginManager().registerEvents(this, this);

        if(mainConfig.shouldCheckForUpdates()) {
            checkForUpdates();
        }

        info("Heads plugin enabled with " + cache.getHeadCount() + " heads " + timer);
    }

    @Override
    public void onDisable() {
        instance = null;

        unregisterCommands();
    }

    private void checkForUpdates() {
        async(() -> {
            try {
                String currentVersion = UpdateChecker.getCurrentVersion();
                String latestVersion = UpdateChecker.getLatestVersion();

                if(!UpdateChecker.isNewerVersion(latestVersion))
                    return;

                warning("A newer version of Heads, Heads v" + latestVersion + ", is available for download");
                warning("You are currently using Heads v" + currentVersion);
            } catch(IOException e) {
                severe("There was an error checking for an update for Heads");
            }
        });
    }

    private void registerCommands() {
        if (commandsRegistered) {
            unregisterCommands();
        }

        SimpleCommandMap commandMap = CraftServer.get().getCommandMap();

        RuntimeCommand heads = new RuntimeCommand(mainConfig.getHeadCommand());
        heads.setExecutor(new HeadsCommand());
        heads.setDescription(mainConfig.getHeadDescription());
        heads.setAliases(Arrays.asList(mainConfig.getHeadAliases()));

        commandMap.register("heads", heads);

        commandsRegistered = true;
    }

    private void unregisterCommands() {
        SimpleCommandMap commandMap = CraftServer.get().getCommandMap();
        Map<String, Command> map = CommandMap.getCommandMap(commandMap);

        map.values().removeIf(command -> command instanceof RuntimeCommand);

        commandsRegistered = false;
    }

    public void reloadConfigs() {
        oldMenuConfig.reload();
        menus.reload();
        langConfig.reload();
        mainConfig.reload();

        registerCommands();

        economy = hookEconomy();

        tryHookBlockStore();
    }

    public File getCacheFile() {
        if(!getDataFolder().exists() && !getDataFolder().mkdirs())
            throw new RuntimeException("Unable to create the data folder to save plugin files");

        if(!getDataFolder().isDirectory())
            throw new RuntimeException("plugins/Heads should be a directory, yet there is a file with the same name");

        return new File(getDataFolder(), "heads.cache");
    }

    private CacheFile loadCache() {
        File file = getCacheFile();
        FileConfigFile legacyConfig = new FileConfigFile("cache.yml");

        boolean requiresWrite = false;

        if(!file.exists()) {
            requiresWrite = true;

            if(legacyConfig.getFile().exists()) {
                Clock timer = Clock.start();

                LegacyCacheConfig legacy = new LegacyCacheConfig(legacyConfig);
                cache = CacheFileConverter.convertToCacheFile("main-cache", legacy);

                info("Converted legacy yaml cache file to new binary file " + timer);
            } else {
                cache = new CacheFile("main-cache");
            }
        } else {
            try {
                Clock timer = Clock.start();

                cache = CacheFile.read(file);

                info("Loaded cache file " + timer);
            } catch (IOException e) {
                severe("Unable to read heads.cache file");
                throw new RuntimeException("There was an exception reading the heads.cache file", e);
            }
        }

        if(installAddons() || requiresWrite) {
            saveCache();
        }

        if(legacyConfig.getFile().exists() && !legacyConfig.getFile().delete()) {
            severe("Unable to delete legacy yaml cache file");
        }

        return cache;
    }

    public void saveCache() {
        File file = getCacheFile();

        try {
            Clock timer = Clock.start();

            cache.write(file);

            info("Saved cache file " + timer);
        } catch (IOException e) {
            severe("Unable to save the cache to heads.cache");
            throw new RuntimeException("There was an exception saving the cache", e);
        }
    }

    private ModsFileHeader readModsFileHeader() {
        try {
            return ModsFileHeader.readResource("cache.mods");
        } catch (IOException e) {
            severe("Unable to read header of cache.mods");
            throw new RuntimeException("Unable to read header of cache.mods", e);
        }
    }

    private ModsFile readModsFile() {
        try {
            return ModsFile.readResource("cache.mods");
        } catch (IOException e) {
            severe("Unable to read mods from cache.mods");
            throw new RuntimeException("Unable to read mods from cache.mods", e);
        }
    }

    private boolean installAddons() {
        Clock timer = Clock.start();

        ModsFileHeader header = readModsFileHeader();
        int newMods = header.getUninstalledMods(cache);

        if(newMods == 0)
            return false;

        ModsFile mods = readModsFile();

        int newHeads = mods.installMods(cache);

        if(newHeads > 0) {
            info("Added " + newHeads + " new heads from " + newMods + " addons " + timer);
        } else {
            info("Installed " + newMods + " addons " + timer);
        }

        return true;
    }

    private Economy hookEconomy() {
        if(!mainConfig.isEconomyEnabled())
            return new NoEconomy();

        Economy economy = null;

        if(mainConfig.isVaultEconomyEnabled()) {
            economy = tryHookEconomy(null, new VaultEconomy());
        }

        if(mainConfig.isItemEconomyEnabled()) {
            economy = tryHookEconomy(economy, new ItemEconomy());
        }

        if(mainConfig.isPlayerPointsEconomyEnabled()) {
            economy = tryHookEconomy(economy, new PlayerPointsEconomy());
        }

        if(economy == null || economy instanceof NoEconomy) {
            severe("Economy enabled in config.yml yet Vault, PlayerPoints and Item economies disabled. " +
                    "Player's will not be able to purchase heads.");

            economy = (economy != null ? economy : new NoEconomy());
        }

        return economy;
    }

    private Economy tryHookEconomy(Economy currentlyHooked, Economy toHook) {
        if(currentlyHooked != null) {
            warning(toHook.getName() + " economy is not the only economy enabled in the config.yml.");

            if(!(currentlyHooked instanceof NoEconomy))
                return currentlyHooked;
        }

        if(!toHook.tryHook()) {
            severe(toHook.getName() + " enabled in config.yml, yet Heads was unable to hook into it.");
            return new NoEconomy();
        }

        info("Loaded " + toHook.getName() + " economy");
        return toHook;
    }

    private void tryHookBlockStore() {
        if (mainConfig.shouldUseBlockStore() && Bukkit.getPluginManager().getPlugin("BlockStore") != null) {
            blockStoreAvailable = false;

            try {
                Class<?> apiClass = Class.forName("net.sothatsit.blockstore.BlockStoreApi");

                apiClass.getDeclaredMethod("retrieveBlockMeta",
                        Plugin.class, Location.class, Plugin.class, String.class, Consumer.class);

                info("Hooked BlockStore");

                blockStoreAvailable = true;

            } catch (ClassNotFoundException | NoSuchMethodException e) {
                severe("Unable to hook BlockStore, the version of BlockStore you are " +
                        "using may be outdated. Heads requires BlockStore v1.5.0.");
                severe("Please update BlockStore and report this to Sothatsit if the problem persists.");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();

        if(inventory == null)
            return;

        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof ClickInventory) {
            ((ClickInventory) holder).onClick(e);
        } else if (holder instanceof InventoryMenu) {
            ((InventoryMenu) holder).onClick(e);
        }
    }

    public boolean isExemptFromCost(Player player) {
        if(!mainConfig.isEconomyEnabled() || player.hasPermission("heads.bypasscost") )
            return true;

        return mainConfig.isFreeInCreative() && player.getGameMode() == GameMode.CREATIVE;
    }

    public boolean chargeForHead(Player player, CacheHead head) {
        if(isExemptFromCost(player))
            return true;

        double cost = head.getCost();

        if(cost <= 0)
            return true;

        if(!economy.hasBalance(player, cost)) {
            Lang.Menu.Get.notEnoughMoney(head.getName(), head.getCost()).send(player);
            return false;
        }

        if(!economy.takeBalance(player, cost)) {
            Lang.Menu.Get.transactionError(head.getName(), head.getCost()).send(player);
            return false;
        }

        Lang.Menu.Get.purchased(head.getName(), head.getCost()).send(player);
        return true;
    }

    public static String getCategoryPermission(String category) {
        return "heads.category." + category.toLowerCase().replace(' ', '_');
    }

    public static Heads getInstance() {
        return instance;
    }

    public static LegacyIDs getLegacyIDs() {
        return instance.legacyIDs;
    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }
    
    public static CacheFile getCache() {
        return instance.cache;
    }

    public static Menus getMenus() {
        return instance.menus;
    }

    public static MenuConfig getMenuConfig() {
        return instance.oldMenuConfig;
    }
    
    public static LangConfig getLangConfig() {
        return instance.langConfig;
    }

    public static Economy getEconomy() {
        return instance.economy;
    }

    public static boolean isBlockStoreAvailable() {
        return instance.blockStoreAvailable;
    }

    public static void info(String info) {
        instance.getLogger().info(info);
    }
    
    public static void warning(String warning) {
        instance.getLogger().warning(warning);
    }
    
    public static void severe(String severe) {
        instance.getLogger().severe(severe);
    }

    public static void sync(Runnable task) {
        Bukkit.getScheduler().runTask(instance, task);
    }

    public static void async(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(instance, task);
    }

    public static FileConfigFile getVersionedConfig(String resource) {
        if(Version.isBelow(Version.v1_13))
            return new FileConfigFile(resource, "pre1_13/" + resource);

        return new FileConfigFile(resource);
    }

}