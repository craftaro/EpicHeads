package com.craftaro.epicheads.gui;

import com.craftaro.core.gui.Gui;
import com.craftaro.core.gui.GuiUtils;
import com.craftaro.epicheads.EpicHeads;
import com.craftaro.epicheads.head.Category;
import com.craftaro.epicheads.head.Head;
import com.craftaro.epicheads.settings.Settings;
import com.craftaro.third_party.com.cryptomorin.xseries.SkullUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        this.setTitle(this.plugin.getLocale().getMessage("gui.overview.title")
                .processPlaceholder("count", this.plugin.getHeadManager().getHeads().size())
                .getMessage());
        this.setPrevPage(this.rows - 1, 1, GuiUtils.createButtonItem(XMaterial.ARROW,
                this.plugin.getLocale().getMessage("gui.general.previous").getMessage()));
        this.setNextPage(this.rows - 1, 7, GuiUtils.createButtonItem(XMaterial.ARROW,
                this.plugin.getLocale().getMessage("gui.general.next").getMessage()));
        this.setOnPage((event) -> showPage());
        showPage();
    }

    private void showPage() {
        setButton(4, GuiUtils.createButtonItem(XMaterial.GOLDEN_APPLE,
                        this.plugin.getLocale().getMessage("gui.overview.viewfavorites").getMessage(),
                        this.plugin.getLocale().getMessage("gui.overview.favoriteslore").getMessage().split("\\|")),
                (event) -> this.guiManager.showGUI(this.player, new GUIHeads(this.plugin, this.player, null, GUIHeads.QueryTypes.FAVORITES,
                        this.plugin.getPlayerManager().getPlayer(this.player).getFavoritesAsHeads())));

        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial());

        mirrorFill(0, 0, true, true, glass2);
        mirrorFill(1, 0, true, true, glass2);
        mirrorFill(0, 1, true, true, glass2);
        mirrorFill(0, 2, true, true, glass3);

        int numTemplates = this.plugin.getHeadManager().getCategories().size();
        this.pages = (int) Math.floor(numTemplates / 21.0);

        List<Category> categories = this.plugin.getHeadManager()
                .getCategories()
                .stream()
                .skip((this.page - 1) * (this.rows - 1) * 9)
                .limit((this.rows - 1) * 9)
                .collect(Collectors.toList());

        int add = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (i + add == 7 || i + add == 16) {
                add = add + 2;
            }

            Category category = categories.get(i);

            List<Head> heads = category.isLatestPack() ? this.plugin.getHeadManager().getLatestPack() : this.plugin.getHeadManager().getHeadsByCategory(category);
            if (heads.isEmpty()) {
                continue;
            }

            Head firstHead = heads.get(0);

            if (!this.player.hasPermission("epicheads.category." + category.getName().replace(" ", "_"))) {
                continue;
            }

            ItemStack buttonItem = XMaterial.PLAYER_HEAD.parseItem();
            ItemMeta buttonMeta = buttonItem.getItemMeta();
            SkullUtils.applySkin(buttonMeta, firstHead.getUrl());
            buttonItem.setItemMeta(buttonMeta);

            setButton(i + 10 + add, GuiUtils.createButtonItem(buttonItem,
                            this.plugin.getLocale().getMessage("gui.overview.headname")
                                    .processPlaceholder("name", Color.getRandomColor() + category.getName())
                                    .getMessage(),
                            category.isLatestPack() ? this.plugin.getLocale().getMessage("gui.overview.packlore")
                                    .processPlaceholder("pack", firstHead.getPack()).getMessage()
                                    : this.plugin.getLocale().getMessage("gui.overview.headlore")
                                    .processPlaceholder("count", String.format("%,d", category.getCount()))
                                    .getMessage()),
                    (event) ->
                            this.guiManager.showGUI(this.player, new GUIHeads(this.plugin, this.player, category.isLatestPack() ? category.getName() : null,
                                    category.isLatestPack() ? GUIHeads.QueryTypes.PACK : GUIHeads.QueryTypes.CATEGORY, heads)));
        }

        setButton(Settings.DISCORD.getBoolean() ? 39 : 40, GuiUtils.createButtonItem(XMaterial.COMPASS,
                        this.plugin.getLocale().getMessage("gui.overview.search").getMessage()),
                (event) -> GUIHeads.doSearch(this.plugin, this, this.guiManager, event.player));

        if (Settings.DISCORD.getBoolean()) {
            ItemStack discordButtonItem = XMaterial.PLAYER_HEAD.parseItem();
            ItemMeta discordButtonMeta = discordButtonItem.getItemMeta();
            SkullUtils.applySkin(discordButtonMeta, "a3b183b148b9b4e2b158334aff3b5bb6c2c2dbbc4d67f76a7be856687a2b623");
            discordButtonItem.setItemMeta(discordButtonMeta);

            setButton(41, GuiUtils.createButtonItem(discordButtonItem,
                            this.plugin.getLocale().getMessage("gui.overview.discord").getMessage(),
                            this.plugin.getLocale().getMessage("gui.overview.discordlore").getMessage().split("\\|")),
                    (event) -> {
                        this.plugin.getLocale().newMessage("&9https://songoda.com/discord").sendPrefixedMessage(this.player);
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

        final String color;

        Color(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }

        public static String getRandomColor() {
            Random random = new Random();
            return values()[random.nextInt(values().length)].getColor();
        }
    }
}
