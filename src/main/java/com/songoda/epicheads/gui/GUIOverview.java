package com.songoda.epicheads.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GUIOverview extends Gui {

    private final EpicHeads plugin;
    private final Player player;

    public GUIOverview(Player player) {
        this.plugin = EpicHeads.getInstance();
        this.player = player;

        this.setDefaultItem(null);
        this.setRows(5);
        this.setTitle(plugin.getLocale().getMessage("gui.overview.title")
                .processPlaceholder("count", plugin.getHeadManager().getHeads().size())
                .getMessage());
        this.setPrevPage(rows - 1, 1, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                plugin.getLocale().getMessage("gui.general.previous").getMessage()));
        this.setNextPage(rows - 1, 7, GuiUtils.createButtonItem(CompatibleMaterial.ARROW,
                plugin.getLocale().getMessage("gui.general.next").getMessage()));
        this.setOnPage((event) -> showPage());
        showPage();
    }

    void showPage() {
        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.GOLDEN_APPLE,
                plugin.getLocale().getMessage("gui.overview.viewfavorites").getMessage(),
                plugin.getLocale().getMessage("gui.overview.favoriteslore").getMessage().split("\\|")),
                (event) -> guiManager.showGUI(player, new GUIHeads(plugin, player, null, GUIHeads.QueryTypes.FAVORITES,
                        plugin.getPlayerManager().getPlayer(player).getFavoritesAsHeads())));

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        GuiUtils.mirrorFill(this, 0, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 1, 0, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 1, true, true, glass2);
        GuiUtils.mirrorFill(this, 0, 2, true, true, glass3);

        int numTemplates = plugin.getHeadManager().getCategories().size();
        pages = (int) Math.floor(numTemplates / 21.0);

        List<Category> categories = plugin.getHeadManager().getCategories().stream().skip((page - 1) * (rows - 1) * 9).limit((rows - 1) * 9)
                .collect(Collectors.toList());

        int add = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (i + add == 7 || i + add == 16) add = add + 2;

            Category category = categories.get(i);

            List<Head> heads = category.isLatestPack() ? plugin.getHeadManager().getLatestPack() : plugin.getHeadManager().getHeadsByCategory(category);

            if (heads.isEmpty()) continue;

            Head firstHead = heads.get(0);

            if (!player.hasPermission("epicheads.category." + category.getName().replace(" ", "_"))) continue;

            setButton(i + 10 + add, GuiUtils.createButtonItem(ItemUtils.getCustomHead(firstHead.getURL()),
                    plugin.getLocale().getMessage("gui.overview.headname")
                            .processPlaceholder("name", Color.getRandomColor() + category.getName())
                            .getMessage(),
                    category.isLatestPack() ? plugin.getLocale().getMessage("gui.overview.packlore")
                            .processPlaceholder("pack", firstHead.getPack()).getMessage()
                            : plugin.getLocale().getMessage("gui.overview.headlore")
                            .processPlaceholder("count", String.format("%,d", category.getCount()))
                            .getMessage()),
                    (event) ->
                            guiManager.showGUI(player, new GUIHeads(plugin, player, category.isLatestPack() ? category.getName() : null,
                                    category.isLatestPack() ? GUIHeads.QueryTypes.PACK : GUIHeads.QueryTypes.CATEGORY, heads)));
        }

        setButton(Settings.DISCORD.getBoolean() ? 39 : 40, GuiUtils.createButtonItem(CompatibleMaterial.COMPASS,
                plugin.getLocale().getMessage("gui.overview.search").getMessage()),
                (event) -> GUIHeads.doSearch(plugin, this, guiManager, event.player));

        if (Settings.DISCORD.getBoolean()) {
            setButton(41, GuiUtils.createButtonItem(ItemUtils.getCustomHead(
                    "a3b183b148b9b4e2b158334aff3b5bb6c2c2dbbc4d67f76a7be856687a2b623"),
                    plugin.getLocale().getMessage("gui.overview.discord").getMessage(),
                    plugin.getLocale().getMessage("gui.overview.discordlore").getMessage().split("\\|")),
                    (event) -> {
                        plugin.getLocale().newMessage("&9https://discord.gg/A9TRJQb").sendPrefixedMessage(player);
                        exit();
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
