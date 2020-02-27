package com.songoda.epicheads;

import com.songoda.core.SongodaCore;
import com.songoda.core.SongodaPlugin;
import com.songoda.core.commands.CommandManager;
import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.configuration.Config;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.hooks.PluginHook;
import com.songoda.core.hooks.economies.Economy;
import com.songoda.epicheads.commands.*;
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
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
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

    private Storage storage;

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
        this.storage.closeConnection();
        this.saveToFile();
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

        this.storage = new StorageYaml(this);

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

        // Load Favorites
        loadData();

        int timeout = Settings.AUTOSAVE.getInt() * 60 * 20;
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this::saveToFile, timeout, timeout);
    }

    private void saveToFile() {
        storage.doSave();
    }

    private void loadData() {
        // Adding in favorites.
        if (storage.containsGroup("players")) {
            for (StorageRow row : storage.getRowsByGroup("players")) {
                if (row.get("uuid").asObject() == null)
                    continue;

                EPlayer player = new EPlayer(
                        UUID.fromString(row.get("uuid").asString()),
                        (List<String>) row.get("favorites").asObject());

                this.playerManager.addPlayer(player);
            }
        }

        // Save data initially so that if the person reloads again fast they don't lose all their data.
        this.saveToFile();
    }


    private void downloadHeads() {
        try {
            InputStream is = new URL("http://www.head-db.com/dump").openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONParser parser = new JSONParser();
            JSONArray json = (JSONArray) parser.parse(jsonText);

            try (FileWriter file = new FileWriter(getDataFolder() + "/heads.json")) {
                file.write(json.toJSONString());
            }
        } catch (Exception e) {
            System.out.println("Failed to download heads.");
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

                    if (!tagOptional.isPresent())
                        headManager.addCategory(category);
                    headManager.addLocalHead(head);
                }
            }

            if (storage.containsGroup("disabled")) {
                for (StorageRow row : storage.getRowsByGroup("disabled")) {
                    headManager.disableHead(new Head(row.get("id").asInt(), false));
                }
            }

            // convert disabled heads
            if (config.contains("Main.Disabled Global Heads")) {
                for (int id : config.getIntegerList("Main.Disabled Global Heads")) {
                    EpicHeads.getInstance().getHeadManager().disableHead(new Head(id, false));
                }
                config.set("Main.Disabled Global Heads", null);
            }

            System.out.println("loaded " + headManager.getHeads().size() + " Heads.");

        } catch (IOException e) {
            System.out.println("Heads file not found. Plugin disabling.");
            return false;
        } catch (ParseException e) {
            System.out.println("Failed to parse heads file. Plugin disabling.");
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
        saveToFile();

        this.setLocale(getConfig().getString("System.Language Mode"), true);
        this.locale.reloadMessages();

        saveToFile();
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
}
