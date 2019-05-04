package com.songoda.epicheads.gui;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import org.bukkit.entity.Player;

public class GUIOverview extends AbstractGUI {
    
    private final EpicHeads plugin;
    
    public GUIOverview(EpicHeads plugin, Player player) {
        super(player);
        this.plugin = plugin;

        init("Heads", 54);
    }

    @Override
    protected void constructGUI() {
        
    }

    @Override
    protected void registerClickables() {

    }

    @Override
    protected void registerOnCloses() {

    }
}
