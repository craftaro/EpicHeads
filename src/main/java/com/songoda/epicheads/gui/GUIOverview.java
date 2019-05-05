package com.songoda.epicheads.gui;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Tag;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class GUIOverview extends AbstractGUI {
    
    private final EpicHeads plugin;
    
    public GUIOverview(EpicHeads plugin, Player player) {
        super(player);
        this.plugin = plugin;

        init("EpicHeads (" + plugin.getHeadManager().getHeads().size() + " heads)", 45);
    }

    @Override
    protected void constructGUI() {

        createButton(5, Material.STONE, "&6&lView Favorites",
                "Shift click any head",
                "to save as a favorite.");

        inventory.setItem(0, Methods.getBackgroundGlass(true));
        inventory.setItem(1, Methods.getBackgroundGlass(true));
        inventory.setItem(9, Methods.getBackgroundGlass(true));

        inventory.setItem(7, Methods.getBackgroundGlass(true));
        inventory.setItem(8, Methods.getBackgroundGlass(true));
        inventory.setItem(17, Methods.getBackgroundGlass(true));

        inventory.setItem(27, Methods.getBackgroundGlass(true));
        inventory.setItem(36, Methods.getBackgroundGlass(true));
        inventory.setItem(37, Methods.getBackgroundGlass(true));

        inventory.setItem(35, Methods.getBackgroundGlass(true));
        inventory.setItem(43, Methods.getBackgroundGlass(true));
        inventory.setItem(44, Methods.getBackgroundGlass(true));

        inventory.setItem(2, Methods.getBackgroundGlass(false));
        inventory.setItem(6, Methods.getBackgroundGlass(false));
        inventory.setItem(38, Methods.getBackgroundGlass(false));
        inventory.setItem(42, Methods.getBackgroundGlass(false));

        List<Tag> tags = plugin.getHeadManager().getTags();
        int add = 0;
        for (int i = 0; i < tags.size(); i++) {
            if (i + add == 7 || i + add == 16) add = add + 2;

            Tag tag = plugin.getHeadManager().getTags().get(i);
            createButton(i + 10 + add, Material.STONE, "&c&l" + tag.getName(), "&e" + tag.getCount() + " heads");

            registerClickable(i + 10 + add, ((player1, inventory1, cursor, slot, type) -> new GUIHeads(plugin, player, null, plugin.getHeadManager().getHeadsByTag(tag))));
        }

        createButton(39, Material.COMPASS, "Search");

        createButton(41, Material.STONE,
                "Add or request new heads",
                "in our discord server.");

    }

    @Override
    protected void registerClickables() {
        registerClickable(5, ((player1, inventory1, cursor, slot, type) ->
                new GUIHeads(plugin, player, null, plugin.getPlayerManager().getPlayer(player).getFavoritesAsHeads())));

        registerClickable(39, ((player1, inventory1, cursor, slot, type) ->
                GUIHeads.doSearch(player1)));
    }

    @Override
    protected void registerOnCloses() {

    }
}
