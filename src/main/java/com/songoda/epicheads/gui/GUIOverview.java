package com.songoda.epicheads.gui;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Tag;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import com.songoda.epicheads.utils.SettingsManager;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GUIOverview extends AbstractGUI {
    
    private final EpicHeads plugin;
    
    public GUIOverview(EpicHeads plugin, Player player) {
        super(player);
        this.plugin = plugin;

        init(plugin.getLocale().getMessage("gui.overview.title", plugin.getHeadManager().getHeads().size()), 45);
    }

    @Override
    protected void constructGUI() {

        ArrayList<String> lore = new ArrayList<>();
        String[] parts = plugin.getLocale().getMessage("gui.overview.favoriteslore").split("\\|");
        for (String line : parts)
            lore.add(Methods.formatText(line));

        createButton(4, Material.GOLDEN_APPLE, plugin.getLocale().getMessage("gui.overview.viewfavorites"),
                lore);

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

            if (!player.hasPermission("epicheads.category." + tag.getName())) continue;

            createButton(i + 10 + add, Methods.addTexture(new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                            ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3),
                    plugin.getHeadManager().getHeadsByTag(tag).get(0).getURL()),
                    plugin.getLocale().getMessage("gui.overview.headname", Color.getRandomColor() + tag.getName()),
                    plugin.getLocale().getMessage("gui.overview.headlore", tag.getCount()));

            registerClickable(i + 10 + add, ((player1, inventory1, cursor, slot, type) ->
                    new GUIHeads(plugin, player, null, plugin.getHeadManager().getHeadsByTag(tag))));
        }

        createButton(SettingsManager.Setting.DISCORD.getBoolean() ? 39 : 40, Material.COMPASS, plugin.getLocale().getMessage("gui.overview.search"));


        if (SettingsManager.Setting.DISCORD.getBoolean()) {

            ArrayList<String> lore2 = new ArrayList<>();
            String[] parts2 = plugin.getLocale().getMessage("gui.overview.discordlore").split("\\|");
            for (String line : parts2)
                lore2.add(Methods.formatText(line));

            createButton(41, Methods.addTexture(new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                            ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3),
                    "a3b183b148b9b4e2b158334aff3b5bb6c2c2dbbc4d67f76a7be856687a2b623"),
                    plugin.getLocale().getMessage("gui.overview.discord"),
                    lore2);
        }

    }

    @Override
    protected void registerClickables() {
        registerClickable(4, ((player1, inventory1, cursor, slot, type) ->
                new GUIHeads(plugin, player, plugin.getLocale().getMessage("general.word.favorites"),
                        plugin.getPlayerManager().getPlayer(player).getFavoritesAsHeads())));

        registerClickable(SettingsManager.Setting.DISCORD.getBoolean() ? 39 : 40, ((player1, inventory1, cursor, slot, type) ->
                GUIHeads.doSearch(player1)));

        if (SettingsManager.Setting.DISCORD.getBoolean()) {
            registerClickable(41, ((player1, inventory1, cursor, slot, type) -> {
                player.sendMessage(Methods.formatText(plugin.getReferences().getPrefix() + "&9https://discord.gg/A9TRJQb"));
                player.closeInventory();
            }));
        }
    }

    @Override
    protected void registerOnCloses() {

    }

    public enum Color {
        C9("&9&l"),
        CA("&a&l"),
        CB("&b&l"),
        C8("&8&l"),
        CD("&d&l"),
        CC("&c&l"),
        C6("&6&l");

        String color;

        Color(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public static String getRandomColor() {
            Random random = new Random();
            return values()[random.nextInt(values().length)].getColor();
        }
    }

}
