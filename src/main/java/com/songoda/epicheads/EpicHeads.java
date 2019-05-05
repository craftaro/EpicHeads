package com.songoda.epicheads;

import com.songoda.epicheads.command.CommandManager;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.HeadManager;
import com.songoda.epicheads.head.Tag;
import com.songoda.epicheads.listeners.ItemListeners;
import com.songoda.epicheads.players.PlayerManager;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import com.songoda.epicheads.utils.SettingsManager;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import com.songoda.epicheads.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;

public class EpicHeads extends JavaPlugin {
    private static CommandSender console = Bukkit.getConsoleSender();
    private static EpicHeads INSTANCE;
    private References references;

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

    private HeadManager headManager;
    private PlayerManager playerManager;
    private SettingsManager settingsManager;
    private CommandManager commandManager;

    private Locale locale;

    public static EpicHeads getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicHeads " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &aEnabling&7..."));

        this.settingsManager = new SettingsManager(this);
        this.setupConfig();

        // Setup language
        String langMode = SettingsManager.Setting.LANGUGE_MODE.getString();
        Locale.init(this);
        Locale.saveDefaultLocale("en_US");
        this.locale = Locale.getLocale(getConfig().getString("System.Language Mode", langMode));

        //Running Songoda Updater
        Plugin plugin = new Plugin(this, 26);
        plugin.addModule(new LocaleModule());
        SongodaUpdate.load(plugin);

        this.references = new References();

        // Setup Managers
        this.headManager = new HeadManager();
        this.playerManager = new PlayerManager();
        this.commandManager = new CommandManager(this);

        // Register Listeners
        AbstractGUI.initializeListeners(this);
        Bukkit.getPluginManager().registerEvents(new ItemListeners(this), this);

        // Download Heads
        downloadHeads();

        // Load Heads
        loadHeads();

        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicHeads " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
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
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(getDataFolder() + "/heads.json"));

            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;

                String tags = (String) jsonObject.get("tags");
                Optional<Tag> tagOptional = headManager.getTags().stream().filter(t -> t.getName().equalsIgnoreCase(tags)).findFirst();

                Tag tag = tagOptional.orElseGet(() -> new Tag(tags, -1));

                Head head = new Head(Integer.parseInt((String) jsonObject.get("id")),
                        (String) jsonObject.get("name"),
                        (String) jsonObject.get("url"),
                        tag,
                        Byte.parseByte((String) jsonObject.get("staff_picked")));

                if (head.getName() == null ||
                        head.getName().equals("null")) continue;

                if (!tagOptional.isPresent())
                    headManager.addTag(tag);
                headManager.addHead(head);
            }

            for (Tag tag : headManager.getTags()) {
                tag.setCount(Math.toIntExact(headManager.getHeads().stream().filter(head -> head.getTag() == tag).count()));
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


    public ServerVersion getServerVersion() {
        return serverVersion;
    }

    public boolean isServerVersion(ServerVersion version) {
        return serverVersion == version;
    }
    public boolean isServerVersion(ServerVersion... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public boolean isServerVersionAtLeast(ServerVersion version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    private void setupConfig() {
        settingsManager.updateSettings();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void reload() {
        locale.reloadMessages();
        references = new References();
        this.setupConfig();
        saveConfig();
    }

    public Locale getLocale() {
        return locale;
    }

    public References getReferences() {
        return references;
    }

    public HeadManager getHeadManager() {
        return headManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
