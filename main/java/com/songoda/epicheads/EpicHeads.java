package com.songoda.epicheads;

import com.songoda.epicheads.cache.CacheFile;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.cache.ModsFile;
import com.songoda.epicheads.cache.ModsFileHeader;
import com.songoda.epicheads.cache.legacy.CacheFileConverter;
import com.songoda.epicheads.cache.legacy.LegacyCacheConfig;
import com.songoda.epicheads.command.CommandManager;
import com.songoda.epicheads.config.FileConfigFile;
import com.songoda.epicheads.config.MainConfig;
import com.songoda.epicheads.config.menu.Menus;
import com.songoda.epicheads.config.oldmenu.MenuConfig;
import com.songoda.epicheads.economy.*;
import com.songoda.epicheads.handlers.HeadNamer;
import com.songoda.epicheads.handlers.LegacyIDs;
import com.songoda.epicheads.menu.ui.InventoryMenu;
import com.songoda.epicheads.oldmenu.ClickInventory;
import com.songoda.epicheads.util.Clock;
import com.songoda.epicheads.util.Methods;
import com.songoda.epicheads.volatilecode.injection.ProtocolHackFixer;
import com.songoda.epicheads.volatilecode.reflection.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class EpicHeads extends JavaPlugin implements Listener {

	private static EpicHeads INSTANCE;
	private static ConsoleCommandSender console;
	private CacheFile cache;
	private MenuConfig oldMenuConfig;
	private Menus menus;
	private MainConfig mainConfig;
	private Economy economy;
	private LegacyIDs legacyIDs;
	private boolean blockStoreAvailable = false;

	private References references;
	private CommandManager commandManager;
	private Locale locale;

	@Override
	public void onEnable() {
		console = this.getServer().getConsoleSender();
		INSTANCE = this;
		if (Version.isBelow(Version.v1_8)) {
			Methods.formatText("&c-------------------------------------------------------------------");
			Methods.formatText("&c    EpicHeads no longer supports versions below Minecraft 1.8.     ");
			Methods.formatText("&c          Please switch to Heads version 1.15.1 or before.         ");
			Methods.formatText("&c-------------------------------------------------------------------");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		console.sendMessage(Methods.formatText("&a============================="));
		console.sendMessage(Methods.formatText("&7EpicHeads " + this.getDescription().getVersion() + " by &5Brianna <3!"));
		console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

		Clock timer = Clock.start();
		loadCache();
		try {
			legacyIDs = LegacyIDs.readResource("legacy-ids.txt");
		} catch (IOException exception) {
			legacyIDs = LegacyIDs.EMPTY;
			Methods.formatText("Unable to load legacy IDs to perform conversion from older Spigot versions");
			exception.printStackTrace();
		}

		// Locales
		Locale.init(this);
		Locale.saveDefaultLocale("en_US");
		this.locale = Locale.getLocale(getConfig().getString("Locale", "en_US"));

		this.references = new References();
		this.menus = new Menus();
		this.menus.reload();
		this.oldMenuConfig = new MenuConfig(getVersionedConfig("menus.yml"));
		this.mainConfig = new MainConfig();
		this.economy = hookEconomy();

		this.commandManager = new CommandManager(this);

		ProtocolHackFixer.fix();
		tryHookBlockStore();
		new HeadNamer().registerEvents();
		Bukkit.getPluginManager().registerEvents(this, this);
		console.sendMessage(Methods.formatText(getDescription().getName() + " has been enabled with " + cache.getHeadCount() + " heads " + timer + "."));
		console.sendMessage(Methods.formatText("&a============================="));
	}

	@Override
	public void onDisable() {
		INSTANCE = null;
		console.sendMessage(Methods.formatText("&a============================="));
		console.sendMessage(Methods.formatText("&7EpicHeads " + this.getDescription().getVersion() + " by &5Brianna <3!"));
		console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
		console.sendMessage(Methods.formatText("&a============================="));

	}

	public void reloadConfigs() {
		this.oldMenuConfig.reload();
		this.menus.reload();
		this.mainConfig.reload();
		this.locale.reloadMessages();
		this.economy = hookEconomy();
		this.tryHookBlockStore();
	}

	public File getCacheFile() {
		if (!getDataFolder().exists() && !getDataFolder().mkdirs())
			throw new RuntimeException("Unable to create the data folder to save plugin files");
		if (!getDataFolder().isDirectory())
			throw new RuntimeException("plugins/EpicHeads should be a directory, yet there is a file with the same name");
		return new File(getDataFolder(), "heads.cache");
	}

	private CacheFile loadCache() {
		File file = getCacheFile();
		FileConfigFile legacyConfig = new FileConfigFile("cache.yml");
		boolean requiresWrite = false;
		if (!file.exists()) {
			requiresWrite = true;
			if (legacyConfig.getFile().exists()) {
				Clock timer = Clock.start();
				LegacyCacheConfig legacy = new LegacyCacheConfig(legacyConfig);
				cache = CacheFileConverter.convertToCacheFile("main-cache", legacy);
				Methods.formatText("Converted legacy yaml cache file to new binary file " + timer);
			} else {
				cache = new CacheFile("main-cache");
			}
		} else {
			try {
				Clock timer = Clock.start();
				cache = CacheFile.read(file);
				Methods.formatText("Loaded cache file " + timer);
			} catch (IOException e) {
				Methods.formatText("Unable to read heads.cache file");
				throw new RuntimeException("There was an exception reading the heads.cache file", e);
			}
		}

		if (installAddons() || requiresWrite) {
			saveCache();
		}

		if (legacyConfig.getFile().exists() && !legacyConfig.getFile().delete()) {
			Methods.formatText("Unable to delete legacy yaml cache file");
		}

		return cache;
	}

	public void saveCache() {
		File file = getCacheFile();
		try {
			Clock timer = Clock.start();

			cache.write(file);

			Methods.formatText("Saved cache file " + timer);
		} catch (IOException e) {
			Methods.formatText("Unable to save the cache to heads.cache");
			throw new RuntimeException("There was an exception saving the cache", e);
		}
	}

	private ModsFileHeader readModsFileHeader() {
		try {
			return ModsFileHeader.readResource("cache.mods");
		} catch (IOException e) {
			Methods.formatText("Unable to read header of cache.mods");
			throw new RuntimeException("Unable to read header of cache.mods", e);
		}
	}

	private ModsFile readModsFile() {
		try {
			return ModsFile.readResource("cache.mods");
		} catch (IOException e) {
			Methods.formatText("Unable to read mods from cache.mods");
			throw new RuntimeException("Unable to read mods from cache.mods", e);
		}
	}

	private boolean installAddons() {
		Clock timer = Clock.start();

		ModsFileHeader header = readModsFileHeader();
		int newMods = header.getUninstalledMods(cache);

		if (newMods == 0)
			return false;

		ModsFile mods = readModsFile();

		int newHeads = mods.installMods(cache);

		if (newHeads > 0) {
			Methods.formatText("Added " + newHeads + " new heads from " + newMods + " addons " + timer);
		} else {
			Methods.formatText("Installed " + newMods + " addons " + timer);
		}

		return true;
	}

	private Economy hookEconomy() {
		if (!mainConfig.isEconomyEnabled())
			return new NoEconomy();

		Economy economy = null;

		if (mainConfig.isVaultEconomyEnabled()) {
			economy = tryHookEconomy(null, new VaultEconomy());
		}

		if (mainConfig.isItemEconomyEnabled()) {
			economy = tryHookEconomy(economy, new ItemEconomy());
		}

		if (mainConfig.isPlayerPointsEconomyEnabled()) {
			economy = tryHookEconomy(economy, new PlayerPointsEconomy());
		}

		if (economy == null || economy instanceof NoEconomy) {
			Methods.formatText("Economy enabled in config.yml yet Vault, PlayerPoints and Item economies disabled. " + "Player's will not be able to purchase heads.");

			economy = (economy != null ? economy : new NoEconomy());
		}

		return economy;
	}

	private Economy tryHookEconomy(Economy currentlyHooked, Economy toHook) {
		if (currentlyHooked != null) {
			Methods.formatText(toHook.getName() + " economy is not the only economy enabled in the config.yml.");

			if (!(currentlyHooked instanceof NoEconomy))
				return currentlyHooked;
		}

		if (!toHook.tryHook()) {
			Methods.formatText(toHook.getName() + " enabled in config.yml, yet Heads was unable to hook into it.");
			return new NoEconomy();
		}

		Methods.formatText("Loaded " + toHook.getName() + " economy");
		return toHook;
	}

	private void tryHookBlockStore() {
		if (mainConfig.shouldUseBlockStore() && Bukkit.getPluginManager().getPlugin("BlockStore") != null) {
			blockStoreAvailable = false;

			try {
				Class<?> apiClass = Class.forName("net.sothatsit.blockstore.BlockStoreApi");

				apiClass.getDeclaredMethod("retrieveBlockMeta", Plugin.class, Location.class, Plugin.class, String.class, Consumer.class);

				Methods.formatText("Hooked BlockStore");

				blockStoreAvailable = true;

			} catch (ClassNotFoundException | NoSuchMethodException e) {
				Methods.formatText("Unable to hook BlockStore, the version of BlockStore you are " + "using may be outdated. Heads requires BlockStore v1.5.0.");
				Methods.formatText("Please update BlockStore and report this to Sothatsit if the problem persists.");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inventory = e.getInventory();

		if (inventory == null)
			return;

		InventoryHolder holder = inventory.getHolder();

		if (holder instanceof ClickInventory) {
			((ClickInventory) holder).onClick(e);
		} else if (holder instanceof InventoryMenu) {
			((InventoryMenu) holder).onClick(e);
		}
	}

	public boolean isExemptFromCost(Player player) {
		if (!mainConfig.isEconomyEnabled() || player.hasPermission("EpicHeads.bypasscost"))
			return true;

		return mainConfig.isFreeInCreative() && player.getGameMode() == GameMode.CREATIVE;
	}

	public boolean chargeForHead(Player player, CacheHead head) {
		EpicHeads instance = EpicHeads.getInstance();
		if (isExemptFromCost(player))
			return true;

		double cost = head.getCost();

		if (cost <= 0)
			return true;

		if (!economy.hasBalance(player, cost)) {
			player.sendMessage(instance.getLocale().getMessage("interface.get.notenoughmoney", head.getName(), head.getCost()));
			return false;
		}

		if (!economy.takeBalance(player, cost)) {
			player.sendMessage(instance.getLocale().getMessage("interface.get.transactionerror", head.getName(), head.getCost()));
			return false;
		}

		player.sendMessage(instance.getLocale().getMessage("interface.get.purchased", head.getName(), head.getCost()));
		return true;
	}

	public static String getCategoryPermission(String category) {
		return "EpicHeads.category." + category.toLowerCase().replace(' ', '_');
	}

	//ToDO: these shouldn't be static.
	public static EpicHeads getInstance() {
		return INSTANCE;
	}

	public static LegacyIDs getLegacyIDs() {
		return INSTANCE.legacyIDs;
	}

	public static MainConfig getMainConfig() {
		return INSTANCE.mainConfig;
	}

	public static CacheFile getCache() {
		return INSTANCE.cache;
	}

	public static Menus getMenus() {
		return INSTANCE.menus;
	}

	public static MenuConfig getMenuConfig() {
		return INSTANCE.oldMenuConfig;
	}

	public static Economy getEconomy() {
		return INSTANCE.economy;
	}

	public static boolean isBlockStoreAvailable() {
		return INSTANCE.blockStoreAvailable;
	}

	public static void sync(Runnable task) {
		Bukkit.getScheduler().runTask(INSTANCE, task);
	}

	public static FileConfigFile getVersionedConfig(String resource) {
		if (Version.isBelow(Version.v1_13))
			return new FileConfigFile(resource, "pre1_13/" + resource);

		return new FileConfigFile(resource);
	}


	public CommandManager getCommandManager() {
		return commandManager;
	}

	public Locale getLocale() {
		return locale;
	}

	public References getReferences() {
		return references;
	}
}