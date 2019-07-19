package com.songoda.epicheads.gui;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import com.songoda.epicheads.utils.settings.Setting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GUIOverview extends AbstractGUI {
    
    private final EpicHeads plugin;
    private int page = 0;
    
    public GUIOverview(EpicHeads plugin, Player player) {
        super(player);
        this.plugin = plugin;

        init(plugin.getLocale().getMessage("gui.overview.title")
                .processPlaceholder("count", plugin.getHeadManager().getHeads().size())
                .getMessage(), 45);
    }

    @Override
    protected void constructGUI() {
        inventory.clear();
        resetClickables();
        registerClickables();

        ArrayList<String> lore = new ArrayList<>();
        String[] parts = plugin.getLocale().getMessage("gui.overview.favoriteslore").getMessage().split("\\|");
        for (String line : parts)
            lore.add(Methods.formatText(line));

        createButton(4, Material.GOLDEN_APPLE, plugin.getLocale().getMessage("gui.overview.viewfavorites")
                        .getMessage(), lore);

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

        int numTemplates = plugin.getHeadManager().getCategories().size();
        int maxPage = (int) Math.floor(numTemplates / 21.0);

        List<Category> categories = plugin.getHeadManager().getCategories().stream().skip(page * 21).limit(21)
                .collect(Collectors.toList());

        if (page != 0) {
            createButton(37, Material.ARROW, plugin.getLocale().getMessage("gui.general.previous")
                    .getMessage());
            registerClickable(37, ((player1, inventory1, cursor, slot, type) -> {
                page --;
                constructGUI();
            }));
        }

        if (page != maxPage) {
            createButton(43, Material.ARROW, plugin.getLocale().getMessage("gui.general.next")
                    .getMessage());
            registerClickable(43, ((player1, inventory1, cursor, slot, type) -> {
                page ++;
                constructGUI();
            }));
        }
        int add = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (i + add == 7 || i + add == 16) add = add + 2;

            Category category = plugin.getHeadManager().getCategories().get((page * 21) + i);

            List<Head> heads = category.isLatestPack() ? plugin.getHeadManager().getLatestPack() : plugin.getHeadManager().getHeadsByCategory(category);

            Head firstHead = heads.get(0);

            if (!player.hasPermission("epicheads.category." + category.getName().replace(" ", "_"))) continue;

            createButton(i + 10 + add, Methods.addTexture(new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                            ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3), firstHead.getURL()),
                    plugin.getLocale().getMessage("gui.overview.headname")
                            .processPlaceholder("name", Color.getRandomColor() + category.getName())
                            .getMessage(),
                    category.isLatestPack() ? plugin.getLocale().getMessage("gui.overview.packlore")
                            .processPlaceholder("pack", firstHead.getPack()).getMessage()
                            : plugin.getLocale().getMessage("gui.overview.headlore")
                            .processPlaceholder("count", String.format("%,d", category.getCount()))
                            .getMessage());

            registerClickable(i + 10 + add, ((player1, inventory1, cursor, slot, type) ->
                    new GUIHeads(plugin, player, category.isLatestPack() ? category.getName() : null,
                            category.isLatestPack() ? GUIHeads.QueryTypes.PACK : GUIHeads.QueryTypes.CATEGORY, heads)));
        }

        createButton(Setting.DISCORD.getBoolean() ? 39 : 40, Material.COMPASS, plugin.getLocale().getMessage("gui.overview.search").getMessage());


        if (Setting.DISCORD.getBoolean()) {
            ArrayList<String> lore2 = new ArrayList<>();
            String[] parts2 = plugin.getLocale().getMessage("gui.overview.discordlore")
                    .getMessage().split("\\|");
            for (String line : parts2)
                lore2.add(Methods.formatText(line));

            createButton(41, Methods.addTexture(new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                            ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3),
                    "a3b183b148b9b4e2b158334aff3b5bb6c2c2dbbc4d67f76a7be856687a2b623"),
                    plugin.getLocale().getMessage("gui.overview.discord").getMessage(),
                    lore2);
        }
    }

    @Override
    protected void registerClickables() {
        registerClickable(4, ((player1, inventory1, cursor, slot, type) ->
                new GUIHeads(plugin, player, null, GUIHeads.QueryTypes.FAVORITES,
                        plugin.getPlayerManager().getPlayer(player).getFavoritesAsHeads())));

        registerClickable(Setting.DISCORD.getBoolean() ? 39 : 40, ((player1, inventory1, cursor, slot, type) ->
                GUIHeads.doSearch(player1)));

        if (Setting.DISCORD.getBoolean()) {
            registerClickable(41, ((player1, inventory1, cursor, slot, type) -> {
                plugin.getLocale().newMessage("&9https://discord.gg/A9TRJQb").sendPrefixedMessage(player);
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
