package com.craftaro.epicheads.head;

import com.craftaro.epicheads.EpicHeads;

import java.util.Objects;

public class Category {
    private final String name;
    private final boolean latestPack;

    public Category(String name) {
        this(name, false);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Category category = (Category) obj;

        String comparableThisName = this.name != null ? this.name.toLowerCase() : null;
        String comparableCategoryName = category.name != null ? category.name.toLowerCase() : null;
        return this.latestPack == category.latestPack && Objects.equals(comparableThisName, comparableCategoryName);
    }

    @Override
    public int hashCode() {
        String name = this.name != null ? this.name.toLowerCase() : null;
        return Objects.hash(name, this.latestPack);
    }
}
