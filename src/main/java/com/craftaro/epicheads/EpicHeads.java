package com.craftaro.epicheads;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.SongodaPlugin;
import com.craftaro.core.commands.CommandManager;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.database.DatabaseConnector;
import com.craftaro.core.database.SQLiteConnector;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EconomyManager;
import com.craftaro.core.hooks.PluginHook;
import com.craftaro.core.hooks.economies.Economy;
import com.craftaro.epicheads.commands.CommandAdd;
import com.craftaro.epicheads.commands.CommandBase64;
import com.craftaro.epicheads.commands.CommandEpicHeads;
import com.craftaro.epicheads.commands.CommandGive;
import com.craftaro.epicheads.commands.CommandGiveToken;
import com.craftaro.epicheads.commands.CommandHelp;
import com.craftaro.epicheads.commands.CommandReload;
import com.craftaro.epicheads.commands.CommandSearch;
import com.craftaro.epicheads.commands.CommandSettings;
import com.craftaro.epicheads.commands.CommandUrl;
import com.craftaro.epicheads.database.DataHelper;
import com.craftaro.epicheads.database.migrations._1_InitialMigration;
import com.craftaro.epicheads.database.migrations._2_FixAutoIncrementMigration;
import com.craftaro.epicheads.head.Category;
import com.craftaro.epicheads.head.Head;
import com.craftaro.epicheads.head.HeadManager;
import com.craftaro.epicheads.listeners.DeathListeners;
import com.craftaro.epicheads.listeners.ItemListeners;
import com.craftaro.epicheads.listeners.LoginListeners;
import com.craftaro.epicheads.players.EPlayer;
import com.craftaro.epicheads.players.PlayerManager;
import com.craftaro.epicheads.settings.Settings;
import com.craftaro.epicheads.utils.ItemEconomy;
import com.craftaro.epicheads.utils.storage.Storage;
import com.craftaro.epicheads.utils.storage.StorageRow;
import com.craftaro.epicheads.utils.storage.types.StorageYaml;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EpicHeads extends SongodaPlugin {
    private final GuiManager guiManager = new GuiManager(this);
    private HeadManager headManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private PluginHook itemEconomyHook;

    private DatabaseConnector databaseConnector;

    private boolean doneLoadingHeads = false;

    /**
     * @deprecated Use {@link #getPlugin(Class)} instead
     */
    @Deprecated
    public static EpicHeads getInstance() {
        return getPlugin(EpicHeads.class);
    }

    @Override
    public void onPluginLoad() {
        this.itemEconomyHook = PluginHook.addHook(Economy.class, "EpicHeads", ItemEconomy.class);
    }

    @Override
    public void onPluginDisable() {
        this.dataManager.shutdown();
    }

    @Override
    public void onPluginEnable() {
        SongodaCore.registerPlugin(this, 26, XMaterial.PLAYER_HEAD);

        // Load Economy
        EconomyManager.load();

        // Setup Managers
        this.headManager = new HeadManager();
        this.playerManager = new PlayerManager();

        // Setup Config
        Settings.setupConfig();
        this.setLocale(Settings.LANGUGE_MODE.getString(), false);

        // Set economy preference
        String ecoPreference = Settings.ECONOMY_PLUGIN.getString();
        if (ecoPreference.equalsIgnoreCase("item")) {
            EconomyManager.getManager().setPreferredHook(this.itemEconomyHook);
        } else {
            EconomyManager.getManager().setPreferredHook(ecoPreference);
        }

        // Register commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandEpicHeads(this.guiManager))
                .addSubCommands(
                        new CommandAdd(this),
                        new CommandBase64(this),
                        new CommandGive(this),
                        new CommandGiveToken(this),
                        new CommandHelp(this),
                        new CommandReload(this),
                        new CommandSearch(this, this.guiManager),
                        new CommandSettings(this, this.guiManager),
                        new CommandUrl(this)
                );

        // Register Listeners
        this.guiManager.init();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new DeathListeners(this), this);
        pluginManager.registerEvents(new ItemListeners(this), this);
        pluginManager.registerEvents(new LoginListeners(this), this);

        // Download Heads
        downloadHeads();

        // Load Heads
        loadHeads();

        int timeout = Settings.AUTOSAVE.getInt() * 60 * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, DataHelper::saveAllPlayers, timeout, timeout);
    }

    @Override
    public void onDataLoad() {
        // Database stuff.
        this.databaseConnector = new SQLiteConnector(this);
        this.getLogger().info("Data handler connected using SQLite.");

        initDatabase(new _1_InitialMigration(), new _2_FixAutoIncrementMigration());
        DataHelper.init(this.dataManager);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            // Legacy data! Yay!
            File folder = getDataFolder();
            File dataFile = new File(folder, "data.yml");

            boolean converted = false;
            if (dataFile.exists()) {
                converted = true;
                Storage storage = new StorageYaml(this);
                if (storage.containsGroup("players")) {
                    Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] " + ChatColor.RED +
                            "Conversion process starting. Do NOT turn off your server. " +
                            "EpicHeads hasn't fully loaded yet, so make sure users don't" +
                            "interact with the plugin until the conversion process is complete.");

                    List<EPlayer> players = new ArrayList<>();
                    for (StorageRow row : storage.getRowsByGroup("players")) {
                        if (row.get("uuid").asObject() == null) {
                            continue;
                        }

                        players.add(new EPlayer(
                                UUID.fromString(row.get("uuid").asString()),
                                (List<String>) row.get("favorites").asObject()));
                    }
                    DataHelper.migratePlayers(players);
                }

                if (storage.containsGroup("local")) {
                    for (StorageRow row : storage.getRowsByGroup("local")) {
                        String categoryName = row.get("category").asString();
                        Category category = this.headManager.getOrCreateCategoryByName(categoryName);

                        Head head = new Head(row.get("id").asInt(),
                                row.get("name").asString(),
                                row.get("url").asString(),
                                category,
                                true,
                                null,
                                (byte) 0);

                        DataHelper.createLocalHead(head);
                    }

                    if (storage.containsGroup("disabled")) {
                        List<Integer> ids = new ArrayList<>();
                        for (StorageRow row : storage.getRowsByGroup("disabled")) {
                            ids.add(row.get("id").asInt());
                        }

                        DataHelper.migrateDisabledHead(ids);
                    }
                }

                dataFile.delete();
            }

            final boolean finalConverted = converted;
            this.dataManager.getAsyncPool().submit(() -> {
                if (finalConverted) {
                    Bukkit.getConsoleSender().sendMessage("[" + getDescription().getName() + "] " + ChatColor.GREEN + "Conversion complete :)");
                }

                DataHelper.getLocalHeads((heads) -> {
                    this.headManager.addLocalHeads(heads);
                    getLogger().info("Loaded " + this.headManager.getHeads().size() + " heads");

                    this.doneLoadingHeads = true;
                });

                DataHelper.getDisabledHeads((ids) -> {
                    for (int id : ids) {
                        this.headManager.disableHead(new Head(id, false));
                    }
                });
            }, "create");
        });
    }

    private void downloadHeads() {
        try {
            InputStream is = new URL("https://craftaro.github.io/EpicHeads/heads.json").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(jsonText);

            try (FileWriter file = new FileWriter(new File(getDataFolder(), "heads.json"))) {
                file.write(json.toJSONString());
            }
        } catch (Exception ex) {
            getLogger().warning("Failed to download heads: " + ex.getMessage());
        }
    }

    private boolean loadHeads() {
        try {
            this.headManager.clear();
            this.headManager.addCategory(new Category(getLocale().getMessage("general.word.latestpack").getMessage(), true));

            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(getDataFolder() + "/heads.json"));

            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;

                String headName = (String) jsonObject.get("name");
                String headPack = (String) jsonObject.get("pack");
                if (headName == null || headName.equals("null") || (headPack != null && headPack.equals("null"))) {
                    continue;
                }

                String categoryName = (String) jsonObject.get("tags");
                Category category = this.headManager.getOrCreateCategoryByName(categoryName);

                Head head = new Head(
                        Integer.parseInt((String) jsonObject.get("id")),
                        headName,
                        (String) jsonObject.get("url"),
                        category,
                        false,
                        headPack,
                        Byte.parseByte((String) jsonObject.get("staff_picked"))
                );
                this.headManager.addHead(head);
            }
        } catch (IOException | ParseException ex) {
            getLogger().warning(() -> {
                if (ex instanceof ParseException) {
                    return "Disabling plugin, failed to parse heads: " + ex.getMessage();
                }

                return "Disabling plugin, failed to load heads: " + ex.getMessage();
            });

            return false;
        }

        return true;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @Override
    public void onConfigReload() {
        this.setLocale(getConfig().getString("System.Language Mode"), true);
        this.locale.reloadMessages();

        downloadHeads();
        loadHeads();
    }

    @Override
    public List<Config> getExtraConfig() {
        return null;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public HeadManager getHeadManager() {
        return this.headManager;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public DatabaseConnector getDatabaseConnector() {
        return this.databaseConnector;
    }

    public boolean isDoneLoadingHeads() {
        return this.doneLoadingHeads;
    }
}
