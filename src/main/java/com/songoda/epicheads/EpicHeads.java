package com.songoda.epicheads;

import com.songoda.epicheads.command.CommandManager;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import com.songoda.epicheads.utils.SettingsManager;
import com.songoda.epicheads.utils.updateModules.LocaleModule;
import com.songoda.update.Plugin;
import com.songoda.update.SongodaUpdate;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EpicHeads extends JavaPlugin {
    private static CommandSender console = Bukkit.getConsoleSender();
    private static EpicHeads INSTANCE;
    private References references;

    private ServerVersion serverVersion = ServerVersion.fromPackageName(Bukkit.getServer().getClass().getPackage().getName());

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
        this.commandManager = new CommandManager(this);

        console.sendMessage(Methods.formatText("&a============================="));
    }

    @Override
    public void onDisable() {
        console.sendMessage(Methods.formatText("&a============================="));
        console.sendMessage(Methods.formatText("&7EpicHeads " + this.getDescription().getVersion() + " by &5Songoda <3!"));
        console.sendMessage(Methods.formatText("&7Action: &cDisabling&7..."));
        console.sendMessage(Methods.formatText("&a============================="));
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

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
