package com.songoda.epicheads.utils.storage;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.players.EPlayer;
import com.songoda.epicheads.utils.ConfigWrapper;

import java.util.List;

public abstract class Storage {

    protected final EpicHeads instance;
    protected final ConfigWrapper dataFile;

    public Storage(EpicHeads instance) {
        this.instance = instance;
        this.dataFile = new ConfigWrapper(instance, "", "data.yml");
        this.dataFile.createNewFile(null, "EpicSpawners Data File");
        this.dataFile.getConfig().options().copyDefaults(true);
        this.dataFile.saveConfig();
    }

    public abstract boolean containsGroup(String group);

    public abstract List<StorageRow> getRowsByGroup(String group);

    public abstract void prepareSaveItem(String group, StorageItem... items);

    public void updateData(EpicHeads plugin) {
        // Save game data
        for (EPlayer player : instance.getPlayerManager().getPlayers()) {
            prepareSaveItem("players", new StorageItem("uuid", player.getUuid().toString()),
                    new StorageItem("favorites", player.getFavorites()));
        }

        for (Head head : instance.getHeadManager().getLocalHeads()) {
            prepareSaveItem("local", new StorageItem("url", head.getURL()),
                    new StorageItem("name", head.getName()),
                    new StorageItem("id", head.getId()),
                    new StorageItem("category", head.getCategory().getName()));
        }
    }

    public abstract void doSave();

    public abstract void save();

    public abstract void makeBackup();

    public abstract void closeConnection();

}
