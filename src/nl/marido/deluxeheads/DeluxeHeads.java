package nl.marido.deluxeheads;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
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
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import nl.marido.deluxeheads.cache.CacheFile;
import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.cache.ModsFile;
import nl.marido.deluxeheads.cache.ModsFileHeader;
import nl.marido.deluxeheads.cache.legacy.CacheFileConverter;
import nl.marido.deluxeheads.cache.legacy.LegacyCacheConfig;
import nl.marido.deluxeheads.command.HeadsCommand;
import nl.marido.deluxeheads.command.RuntimeCommand;
import nl.marido.deluxeheads.config.FileConfigFile;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.config.lang.LangConfig;
import nl.marido.deluxeheads.config.menu.Menus;
import nl.marido.deluxeheads.config.oldmenu.MenuConfig;
import nl.marido.deluxeheads.economy.Economy;
import nl.marido.deluxeheads.economy.ItemEconomy;
import nl.marido.deluxeheads.economy.NoEconomy;
import nl.marido.deluxeheads.economy.PlayerPointsEconomy;
import nl.marido.deluxeheads.economy.VaultEconomy;
import nl.marido.deluxeheads.handlers.HeadNamer;
import nl.marido.deluxeheads.handlers.LegacyIDs;
import nl.marido.deluxeheads.handlers.UpdateChecker;
import nl.marido.deluxeheads.menu.ui.InventoryMenu;
import nl.marido.deluxeheads.oldmenu.ClickInventory;
import nl.marido.deluxeheads.util.Clock;
import nl.marido.deluxeheads.volatilecode.injection.ProtocolHackFixer;
import nl.marido.deluxeheads.volatilecode.reflection.Version;
import nl.marido.deluxeheads.volatilecode.reflection.craftbukkit.CommandMap;
import nl.marido.deluxeheads.volatilecode.reflection.craftbukkit.CraftServer;

public class DeluxeHeads extends JavaPlugin implements Listener {

	private static DeluxeHeads instance;
	private static ConsoleCommandSender csender;
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
		if (Version.isBelow(Version.v1_8)) {
			print("&c-------------------------------------------------------------------");
			print("&c    DeluxeHeads no longer supports versions below Minecraft 1.8.");
			print("&c          Please switch to Heads version 1.15.1 or before.      ");
			print("&c-------------------------------------------------------------------");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		instance = this;
		Clock timer = Clock.start();
		loadCache();
		try {
			legacyIDs = LegacyIDs.readResource("legacy-ids.txt");
		} catch (IOException exception) {
			legacyIDs = LegacyIDs.EMPTY;
			print("Unable to load legacy IDs to perform conversion from older Spigot versions");
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
		if (mainConfig.shouldCheckForUpdates()) {
			checkForUpdates();
		}
		print("DeluxeHeads has been enabled with " + cache.getHeadCount() + " heads " + timer + ".");
	}

	@Override
	public void onDisable() {
		instance = null;
		unregisterCommands();
	}

	private void checkForUpdates() {
		new BukkitRunnable() {
			public void run() {
				try {
					String currentVersion = UpdateChecker.getCurrentVersion();
					String latestVersion = UpdateChecker.getLatestVersion();
					if (!UpdateChecker.isNewerVersion(latestVersion)) {
						String newversion = Lang.Updater.newVersion().toString();
						newversion = newversion.replaceAll("%version%", currentVersion);
						print(newversion);
						return;
					}
					String oldversion = Lang.Updater.oldVersion().toString();
					oldversion = oldversion.replaceAll("%version%", currentVersion);
					print(oldversion);
				} catch (IOException e) {
					print("&cThere was an error checking for an update for Heads");
				}
			}
		}.runTaskAsynchronously(DeluxeHeads.getInstance());
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
		if (!getDataFolder().exists() && !getDataFolder().mkdirs())
			throw new RuntimeException("Unable to create the data folder to save plugin files");
		if (!getDataFolder().isDirectory())
			throw new RuntimeException("plugins/Heads should be a directory, yet there is a file with the same name");
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
				print("Converted legacy yaml cache file to new binary file " + timer);
			} else {
				cache = new CacheFile("main-cache");
			}
		} else {
			try {
				Clock timer = Clock.start();
				cache = CacheFile.read(file);
				print("Loaded cache file " + timer);
			} catch (IOException e) {
				print("Unable to read heads.cache file");
				throw new RuntimeException("There was an exception reading the heads.cache file", e);
			}
		}

		if (installAddons() || requiresWrite) {
			saveCache();
		}

		if (legacyConfig.getFile().exists() && !legacyConfig.getFile().delete()) {
			print("Unable to delete legacy yaml cache file");
		}

		return cache;
	}

	public void saveCache() {
		File file = getCacheFile();
		try {
			Clock timer = Clock.start();

			cache.write(file);

			print("Saved cache file " + timer);
		} catch (IOException e) {
			print("Unable to save the cache to heads.cache");
			throw new RuntimeException("There was an exception saving the cache", e);
		}
	}

	private ModsFileHeader readModsFileHeader() {
		try {
			return ModsFileHeader.readResource("cache.mods");
		} catch (IOException e) {
			print("Unable to read header of cache.mods");
			throw new RuntimeException("Unable to read header of cache.mods", e);
		}
	}

	private ModsFile readModsFile() {
		try {
			return ModsFile.readResource("cache.mods");
		} catch (IOException e) {
			print("Unable to read mods from cache.mods");
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
			print("Added " + newHeads + " new heads from " + newMods + " addons " + timer);
		} else {
			print("Installed " + newMods + " addons " + timer);
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
			print("Economy enabled in config.yml yet Vault, PlayerPoints and Item economies disabled. " + "Player's will not be able to purchase heads.");

			economy = (economy != null ? economy : new NoEconomy());
		}

		return economy;
	}

	private Economy tryHookEconomy(Economy currentlyHooked, Economy toHook) {
		if (currentlyHooked != null) {
			print(toHook.getName() + " economy is not the only economy enabled in the config.yml.");

			if (!(currentlyHooked instanceof NoEconomy))
				return currentlyHooked;
		}

		if (!toHook.tryHook()) {
			print(toHook.getName() + " enabled in config.yml, yet Heads was unable to hook into it.");
			return new NoEconomy();
		}

		print("Loaded " + toHook.getName() + " economy");
		return toHook;
	}

	private void tryHookBlockStore() {
		if (mainConfig.shouldUseBlockStore() && Bukkit.getPluginManager().getPlugin("BlockStore") != null) {
			blockStoreAvailable = false;

			try {
				Class<?> apiClass = Class.forName("net.sothatsit.blockstore.BlockStoreApi");

				apiClass.getDeclaredMethod("retrieveBlockMeta", Plugin.class, Location.class, Plugin.class, String.class, Consumer.class);

				print("Hooked BlockStore");

				blockStoreAvailable = true;

			} catch (ClassNotFoundException | NoSuchMethodException e) {
				print("Unable to hook BlockStore, the version of BlockStore you are " + "using may be outdated. Heads requires BlockStore v1.5.0.");
				print("Please update BlockStore and report this to Sothatsit if the problem persists.");
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
		if (!mainConfig.isEconomyEnabled() || player.hasPermission("heads.bypasscost"))
			return true;

		return mainConfig.isFreeInCreative() && player.getGameMode() == GameMode.CREATIVE;
	}

	public boolean chargeForHead(Player player, CacheHead head) {
		if (isExemptFromCost(player))
			return true;

		double cost = head.getCost();

		if (cost <= 0)
			return true;

		if (!economy.hasBalance(player, cost)) {
			Lang.Menu.Get.notEnoughMoney(head.getName(), head.getCost()).send(player);
			return false;
		}

		if (!economy.takeBalance(player, cost)) {
			Lang.Menu.Get.transactionError(head.getName(), head.getCost()).send(player);
			return false;
		}

		Lang.Menu.Get.purchased(head.getName(), head.getCost()).send(player);
		return true;
	}

	public static String getCategoryPermission(String category) {
		return "heads.category." + category.toLowerCase().replace(' ', '_');
	}

	public static DeluxeHeads getInstance() {
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

	public static void sync(Runnable task) {
		Bukkit.getScheduler().runTask(instance, task);
	}

	public static void print(String print) {
		if (print != null)
			csender.sendMessage(ChatColor.translateAlternateColorCodes('&', print));
	}

	public static FileConfigFile getVersionedConfig(String resource) {
		if (Version.isBelow(Version.v1_13))
			return new FileConfigFile(resource, "pre1_13/" + resource);

		return new FileConfigFile(resource);
	}

}