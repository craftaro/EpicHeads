package com.songoda.epicheads;

import com.songoda.core.SongodaCore;
import com.songoda.core.SongodaPlugin;
import com.songoda.core.commands.CommandManager;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.database.DataMigrationManager;
import com.songoda.core.database.DatabaseConnector;
import com.songoda.core.database.SQLiteConnector;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.PluginHook;
import com.songoda.core.hooks.economies.Economy;
import com.songoda.epicheads.commands.*;
import com.songoda.epicheads.database.DataManager;
import com.songoda.epicheads.database.migrations._1_InitialMigration;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.HeadManager;
import com.songoda.epicheads.listeners.DeathListeners;
import com.songoda.epicheads.listeners.ItemListeners;
import com.songoda.epicheads.listeners.LoginListeners;
import com.songoda.epicheads.players.EPlayer;
import com.songoda.epicheads.players.PlayerManager;
import com.songoda.epicheads.settings.Settings;
import com.songoda.epicheads.utils.storage.Storage;
import com.songoda.epicheads.utils.storage.StorageRow;
import com.songoda.epicheads.utils.storage.types.StorageYaml;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EpicHeads extends SongodaPlugin {
    private static EpicHeads INSTANCE;

    private final GuiManager guiManager = new GuiManager(this);
    private HeadManager headManager;
    private PlayerManager playerManager;
    private CommandManager commandManager;
    private PluginHook itemEconomyHook;

    private DatabaseConnector databaseConnector;
    private DataManager dataManager;

    public static EpicHeads getInstance() {
        return INSTANCE;
    }

    @Override
    public void onPluginLoad() {
        INSTANCE = this;
        this.itemEconomyHook = PluginHook.addHook(Economy.class, "EpicHeads", com.songoda.epicheads.utils.ItemEconomy.class);
    }

    @Override
    public void onPluginDisable() {
        shutdownDataManager(this.dataManager);
        this.databaseConnector.closeConnection();
    }

    @Override
    public void onPluginEnable() {
        // Run Songoda Updater
        SongodaCore.registerPlugin(this, 26, CompatibleMaterial.PLAYER_HEAD);

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
            EconomyManager.getManager().setPreferredHook(itemEconomyHook);
        } else {
            EconomyManager.getManager().setPreferredHook(ecoPreference);
        }

        // Register commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addCommand(new CommandEpicHeads(guiManager))
                .addSubCommands(
                        new CommandAdd(this),
                        new CommandBase64(this),
                        new CommandGive(this),
                        new CommandGiveToken(this),
                        new CommandHelp(this),
                        new CommandReload(this),
                        new CommandSearch(guiManager),
                        new CommandSettings(guiManager),
                        new CommandUrl(this)
                );

        // Register Listeners
        guiManager.init();
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new DeathListeners(this), this);
        pluginManager.registerEvents(new ItemListeners(this), this);
        pluginManager.registerEvents(new LoginListeners(this), this);

        // Download Heads
        downloadHeads();

        // Load Heads
        loadHeads();

        int timeout = Settings.AUTOSAVE.getInt() * 60 * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> dataManager.saveAllPlayers(), timeout, timeout);
    }

    @Override
    public void onDataLoad() {
        // Database stuff.
        this.databaseConnector = new SQLiteConnector(this);
        this.getLogger().info("Data handler connected using SQLite.");

        this.dataManager = new DataManager(this.databaseConnector, this);
        DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
                new _1_InitialMigration());
        dataMigrationManager.runMigrations();

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            // Legacy data! Yay!
            File folder = getDataFolder();
            File dataFile = new File(folder, "data.yml");

            boolean converted = false;
            if (dataFile.exists()) {
                converted = true;
                Storage storage = new StorageYaml(this);
                if (storage.containsGroup("players")) {
                    console.sendMessage("[" + getDescription().getName() + "] " + ChatColor.RED +
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
                    dataManager.migratePlayers(players);
                }

                if (storage.containsGroup("local")) {
                    for (StorageRow row : storage.getRowsByGroup("local")) {
                        String tagStr = row.get("category").asString();

                        Optional<Category> tagOptional = headManager.getCategories().stream()
                                .filter(t -> t.getName().equalsIgnoreCase(tagStr)).findFirst();

                        Category category = tagOptional.orElseGet(() -> new Category(tagStr));

                        Head head = new Head(row.get("id").asInt(),
                                row.get("name").asString(),
                                row.get("url").asString(),
                                category,
                                true,
                                null,
                                (byte) 0);

                        dataManager.createLocalHead(head);
                    }

                    if (storage.containsGroup("disabled")) {
                        List<Integer> ids = new ArrayList<>();
                        for (StorageRow row : storage.getRowsByGroup("disabled")) {
                            ids.add(row.get("id").asInt());
                        }

                        dataManager.migrateDisabledHead(ids);
                    }
                }

                dataFile.delete();
            }

            final boolean finalConverted = converted;
            dataManager.queueAsync(() -> {
                if (finalConverted) {
                    console.sendMessage("[" + getDescription().getName() + "] " + ChatColor.GREEN + "Conversion complete :)");
                }

                this.dataManager.getLocalHeads((heads) -> {
                    this.headManager.addLocalHeads(heads);
                    getLogger().info("Loaded " + headManager.getHeads().size() + " heads");
                });

                this.dataManager.getDisabledHeads((ids) -> {
                    for (int id : ids) {
                        headManager.disableHead(new Head(id, false));
                    }
                });
            }, "create");
        });
    }

    private void downloadHeads() {
        try {
            InputStream is = new URL("http://www.head-db.com/dump").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(jsonText);

            try (FileWriter file = new FileWriter(getDataFolder() + "/heads.json")) {
                file.write(json.toJSONString());
            }
        } catch (Exception ex) {
            getLogger().warning("Failed to download heads: " + ex.getMessage());
        }
    }

    private boolean loadHeads() {
        try {
            this.headManager.clear();
            this.headManager.addCategory(new Category(getLocale()
                    .getMessage("general.word.latestpack").getMessage(), true));

            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(getDataFolder() + "/heads.json"));

            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;

                String categoryStr = (String) jsonObject.get("tags");
                Optional<Category> tagOptional = headManager.getCategories().stream().filter(t -> t.getName().equalsIgnoreCase(categoryStr)).findFirst();

                Category category = tagOptional.orElseGet(() -> new Category(categoryStr));

                int id = Integer.parseInt((String) jsonObject.get("id"));

                Head head = new Head(id,
                        (String) jsonObject.get("name"),
                        (String) jsonObject.get("url"),
                        category,
                        false,
                        (String) jsonObject.get("pack"),
                        Byte.parseByte((String) jsonObject.get("staff_picked")));

                if (head.getName() == null || head.getName().equals("null")
                        || head.getPack() != null && head.getPack().equals("null")) continue;

                if (!tagOptional.isPresent())
                    headManager.addCategory(category);
                headManager.addHead(head);
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
        return commandManager;
    }

    public HeadManager getHeadManager() {
        return headManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
