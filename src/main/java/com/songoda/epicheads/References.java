package com.songoda.epicheads;

public class References {

    private String prefix;

    public References() {
        prefix = EpicHeads.getInstance().getLocale().getMessage("general.nametag.prefix") + " ";
    }

    public String getPrefix() {
        return this.prefix;
    }
}
