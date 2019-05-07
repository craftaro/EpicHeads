package com.songoda.epicheads.head;

import com.songoda.epicheads.EpicHeads;

public class Tag {

    private final String name;

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return Math.toIntExact(EpicHeads.getInstance().getHeadManager()
                .getHeads().stream().filter(head -> head.getTag() == this).count());
    }
}
