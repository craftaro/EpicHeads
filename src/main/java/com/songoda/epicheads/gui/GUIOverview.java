package com.songoda.epicheads.gui;

import com.songoda.core.compatibility.LegacyMaterials;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.settings.Settings;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GUIOverview extends Gui {

    private final EpicHeads plugin;
    private final Player player;
    private int page = 0;

    public GUIOverview(EpicHeads plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        this.setDefaultItem(null);
        this.setRows(5);
        this.setTitle(plugin.getLocale().getMessage("gui.overview.title")
                .processPlaceholder("count", plugin.getHeadManager().getHeads().size())
                .getMessage());
        showPage();
    }

    void showPage() {
        ArrayList<String> lore = new ArrayList<>();
        String[] parts = plugin.getLocale().getMessage("gui.overview.favoriteslore").getMessage().split("\\|");
        for (String line : parts)
            lore.add(Methods.formatText(line));

        setButton(4, GuiUtils.createButtonItem(LegacyMaterials.GOLDEN_APPLE,
                plugin.getLocale().getMessage("gui.overview.viewfavorites").getMessage(), lore),
                (event) -> plugin.getGuiManager().showGUI(player, new GUIHeads(plugin, player, null, GUIHeads.QueryTypes.FAVORITES,
                                plugin.getPlayerManager().getPlayer(player).getFavoritesAsHeads())));


        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 1, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);

        int numTemplates = plugin.getHeadManager().getCategories().size();
        int maxPage = (int) Math.floor(numTemplates / 21.0);

        List<Category> categories = plugin.getHeadManager().getCategories().stream().skip(page * 21).limit(21)
                .collect(Collectors.toList());

        if (page != 0)
            setButton(37, GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    plugin.getLocale().getMessage("gui.general.previous").getMessage()),
                    (event) -> {
                        page--;
                        showPage();
                    });

        if (page != maxPage)
            setButton(43, GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    plugin.getLocale().getMessage("gui.general.next").getMessage()),
                    (event) -> {
                        page++;
                        showPage();
                    });

        int add = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (i + add == 7 || i + add == 16) add = add + 2;

            Category category = plugin.getHeadManager().getCategories().get((page * 21) + i);

            List<Head> heads = category.isLatestPack() ? plugin.getHeadManager().getLatestPack() : plugin.getHeadManager().getHeadsByCategory(category);

            Head firstHead = heads.get(0);

            if (!player.hasPermission("epicheads.category." + category.getName().replace(" ", "_"))) continue;

            setButton(i + 10 + add, GuiUtils.createButtonItem(Methods.addTexture(new ItemStack(LegacyMaterials.PLAYER_HEAD.getMaterial(), 1, (byte) 3), firstHead.getURL()),
                    plugin.getLocale().getMessage("gui.overview.headname")
                            .processPlaceholder("name", Color.getRandomColor() + category.getName())
                            .getMessage(),
                    category.isLatestPack() ? plugin.getLocale().getMessage("gui.overview.packlore")
                            .processPlaceholder("pack", firstHead.getPack()).getMessage()
                            : plugin.getLocale().getMessage("gui.overview.headlore")
                            .processPlaceholder("count", String.format("%,d", category.getCount()))
                            .getMessage()),
                    (event) ->
                            plugin.getGuiManager().showGUI(player, new GUIHeads(plugin, player, category.isLatestPack() ? category.getName() : null,
                                    category.isLatestPack() ? GUIHeads.QueryTypes.PACK : GUIHeads.QueryTypes.CATEGORY, heads)));
        }

        setButton(Settings.DISCORD.getBoolean() ? 39 : 40, GuiUtils.createButtonItem(LegacyMaterials.COMPASS,
                plugin.getLocale().getMessage("gui.overview.search").getMessage()),
                (event) -> GUIHeads.doSearch(plugin, event.player));


        if (Settings.DISCORD.getBoolean()) {
            ArrayList<String> lore2 = new ArrayList<>();
            String[] parts2 = plugin.getLocale().getMessage("gui.overview.discordlore")
                    .getMessage().split("\\|");
            for (String line : parts2)
                lore2.add(Methods.formatText(line));

            setButton(41, GuiUtils.createButtonItem(Methods.addTexture(new ItemStack(LegacyMaterials.PLAYER_HEAD.getMaterial(), 1, (byte) 3),
                    "a3b183b148b9b4e2b158334aff3b5bb6c2c2dbbc4d67f76a7be856687a2b623"),
                    plugin.getLocale().getMessage("gui.overview.discord").getMessage(),
                    lore2),
                    (event) -> {
                        plugin.getLocale().newMessage("&9https://discord.gg/A9TRJQb").sendPrefixedMessage(player);
                        player.closeInventory();
                    });
        }
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
