package com.songoda.epicheads.players;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private static final Map<UUID, EPlayer> registeredHeads = new HashMap<>();

    public EPlayer getPlayer(UUID uuid) {
        return registeredHeads.computeIfAbsent(uuid, u -> new EPlayer(uuid));
    }

    public EPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public EPlayer addPlayer(EPlayer player) {
        registeredHeads.put(player.getUuid(), player);
        return player;
    }

    public List<EPlayer> getPlayers() {
        return new ArrayList<>(registeredHeads.values());
    }

}
