package com.songoda.epicheads.head;

import com.songoda.epicheads.EpicHeads;

public class Category {
    private final String name;
    private boolean latestPack = false;

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, boolean latestPack) {
        this.name = name;
        this.latestPack = latestPack;
    }

    public String getName() {
        return this.name;
    }

    public boolean isLatestPack() {
        return this.latestPack;
    }

    public int getCount() {
        return Math.toIntExact(EpicHeads.getInstance()
                .getHeadManager()
                .getHeads()
                .stream()
                .filter(head -> head.getCategory() == this)
                .count());
    }
}
