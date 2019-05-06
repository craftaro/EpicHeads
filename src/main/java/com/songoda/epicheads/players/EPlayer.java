package com.songoda.epicheads.players;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EPlayer {

    private final UUID uuid;

    private List<Integer> favorites = new ArrayList<>();

    public EPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public EPlayer(UUID uuid, List<Integer> favorites) {
        this.uuid = uuid;
        this.favorites = favorites;
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<Integer> getFavorites() {
        return new ArrayList<>(favorites);
    }

    public List<Head> getFavoritesAsHeads() {
        return EpicHeads.getInstance().getHeadManager().getHeads().stream()
                .filter(head -> favorites.contains(head.getId())).collect(Collectors.toList());
    }

    public void addFavorite(Integer integer) {
        favorites.add(integer);
    }

    public void removeFavorite(Integer integer) {
        favorites.remove(integer);
    }

}
