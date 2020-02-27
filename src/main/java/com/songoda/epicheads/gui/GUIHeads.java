package com.songoda.epicheads.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiManager;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.hooks.EconomyManager;
import com.songoda.core.input.ChatPrompt;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.players.EPlayer;
import com.songoda.epicheads.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUIHeads extends Gui {

    private final EpicHeads plugin;
    private final Player player;

    private List<Head> heads;

    private String query;
    private final QueryTypes type;

    public GUIHeads(EpicHeads plugin, Player player, String query, QueryTypes type, List<Head> heads) {
        this.plugin = plugin;
        this.player = player;
        this.query = query;
        this.type = type;

        List<String> favorites = plugin.getPlayerManager().getPlayer(player).getFavorites();
        this.heads = heads.stream()
                .sorted(Comparator.comparingInt(head -> (favorites.contains(head.getURL()) ? 0 : 1)))
                .collect(Collectors.toList());

        this.setDefaultItem(null);
        this.setRows(6);
        this.setOnPage((event) -> showPage());
        showPage();
    }

    private void updateTitle() {
        int numHeads = this.heads.size();
        if (numHeads == 0) {
            plugin.getLocale().getMessage("general.search.nonefound").sendPrefixedMessage(player);
            return;
        }
        Category category = heads.get(0).getCategory();

        String name = null;

        switch (type) {
            case SEARCH:
                name = plugin.getLocale().getMessage("general.word.query") + ": " + query;
                break;
            case CATEGORY:
                name = category.getName();
                break;
            case FAVORITES:
                name = plugin.getLocale().getMessage("general.word.favorites").getMessage();
                break;
            case PACK:
                name = plugin.getLocale().getMessage("general.phrase.latestpack").getMessage();
                break;
        }

        pages = (int) Math.floor(numHeads / 45.0);

        this.setTitle(name + " (" + numHeads + ") " + plugin.getLocale().getMessage("general.word.page") + " " + (page) + "/" + (pages));
    }

    void showPage() {
        updateTitle();
        List<Head> pageHeads = this.heads.stream().skip((page - 1) * (rows - 1) * 9).limit((rows - 1) * 9)
                .collect(Collectors.toList());

        if (page - 3 >= 1) {
            setButton(0, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, page - 3,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page - 3)),
                    (event) -> changePage(-3));
        } else {
            clearActions(0);
            setItem(0, null);
        }

        if (page - 2 >= 1) {
            setButton(1, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, page - 2,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page - 2)),
                    (event) -> changePage(-2));
        } else {
            clearActions(1);
            setItem(1, null);
        }

        if (page > 1) {
            setButton(2, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, page - 1,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page - 1)),
                    (event) -> changePage(-1));
        } else {
            clearActions(2);
            setItem(2, null);
        }

        setButton(3, GuiUtils.createButtonItem(CompatibleMaterial.COMPASS,
                plugin.getLocale().getMessage("gui.heads.search").getMessage()),
                (event) -> doSearch(plugin, this, guiManager, event.player));

        setButton(4, GuiUtils.createButtonItem(CompatibleMaterial.MAP, page,
                plugin.getLocale().getMessage("gui.heads.categories").getMessage()), (event) -> guiManager.showGUI(player, new GUIOverview(event.player)));

        if (pageHeads.size() > 1)
            setButton(5, GuiUtils.createButtonItem(CompatibleMaterial.COMPASS,
                    plugin.getLocale().getMessage("gui.heads.refine").getMessage()),
                    (event) -> {
                        exit();
                        ChatPrompt.showPrompt(plugin, event.player, plugin.getLocale().getMessage("general.search.refine").getPrefixedMessage(), promptEvent -> {
                            this.page = 1;
                            this.heads = this.heads.stream().filter(head -> head.getName().toLowerCase()
                                    .contains(promptEvent.getMessage().toLowerCase())).collect(Collectors.toList());
                            if (query == null)
                                this.query = promptEvent.getMessage();
                            else
                                this.query += ", " + promptEvent.getMessage();
                        }).setOnClose(() -> {
                            showPage();
                            guiManager.showGUI(event.player, this);
                        }).setOnCancel(() -> {
                            event.player.sendMessage(plugin.getLocale().getMessage("general.search.canceled").getPrefixedMessage());
                        });
                    });

        if (page + 1 <= pages) {
            setButton(6, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, page + 1,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page + 1)),
                    (event) -> changePage(+1));
        } else {
            clearActions(6);
            setItem(6, null);
        }

        if (page + 2 <= pages) {
            setButton(7, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, page + 2,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page + 2)),
                    (event) -> changePage(+2));
        } else {
            clearActions(7);
            setItem(7, null);
        }

        if (page + 3 <= pages) {
            setButton(8, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, page + 3,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page + 3)),
                    (event) -> changePage(+3));
        } else {
            clearActions(8);
            setItem(8, null);
        }

        List<String> favorites = plugin.getPlayerManager().getPlayer(player).getFavorites();

        double cost = Settings.HEAD_COST.getDouble();
        boolean free = player.hasPermission("epicheads.bypasscost")
                || (Settings.FREE_IN_CREATIVE.getBoolean() && player.getGameMode() == GameMode.CREATIVE);
        int i = 0;
        for (; i < pageHeads.size(); i++) {
            Head head = pageHeads.get(i);

            if (head.getName() == null) continue;

            ItemStack item = head.asItemStack(favorites.contains(head.getURL()), free);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().getLore();
            lore.add(plugin.getLocale().getMessage("gui.heads.delete").getMessage());
            meta.setLore(lore);
            item.setItemMeta(meta);

            setButton(i + 9, item, (event) -> {
                if (event.clickType == ClickType.MIDDLE && player.hasPermission("epicheads.delete")) {
                    plugin.getHeadManager().disableHead(head);
                    heads.remove(head);
                    showPage();
                    return;
                } else if (event.clickType == ClickType.SHIFT_LEFT || event.clickType == ClickType.SHIFT_RIGHT) {
                    EPlayer ePlayer = plugin.getPlayerManager().getPlayer(player);
                    boolean isFav = ePlayer.getFavorites().contains(head.getURL());
                    if (isFav)
                        ePlayer.removeFavorite(head.getURL());
                    else
                        ePlayer.addFavorite(head.getURL());
                    showPage();
                    return;
                }
                if (!free) {
                    if (EconomyManager.isEnabled()) {
                        if (EconomyManager.hasBalance(player, cost)) {
                            EconomyManager.withdrawBalance(player, cost);
                        } else {
                            player.sendMessage(plugin.getLocale().getMessage("event.buyhead.cannotafford").getMessage());
                            return;
                        }
                    } else {
                        player.sendMessage("Economy plugin not setup correctly...");
                        return;
                    }
                }

                ItemStack headItem = item.clone();
                meta.setLore(new ArrayList<>());
                headItem.setItemMeta(meta);

                player.getInventory().addItem(headItem);
            });
        }
        if(inventory != null) {
            i += 9;
            for(; i < this.inventory.getSize(); ++i) {
                clearActions(i);
                setItem(i, null);
            }
        }
    }

    public static void doSearch(EpicHeads plugin, Gui activeGui, GuiManager guiManager, Player player) {
        if (activeGui != null)
            activeGui.exit();
        ChatPrompt.showPrompt(plugin, player, plugin.getLocale().getMessage("general.search.global").getPrefixedMessage(), response -> {
            List<Head> searchHeads = plugin.getHeadManager().getHeads().stream()
                    .filter(head -> head.getName().toLowerCase().contains(response.getMessage().toLowerCase()))
                    .collect(Collectors.toList());
            Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeads.getInstance(), ()
                    -> guiManager.showGUI(player, new GUIHeads(plugin, player, response.getMessage(), QueryTypes.SEARCH, searchHeads)), 0L);
        }).setOnCancel(() -> {
            player.sendMessage(plugin.getLocale().getMessage("general.search.canceled").getPrefixedMessage());
        });
    }

    public static enum QueryTypes {
        SEARCH, CATEGORY, FAVORITES, PACK
    }
}
