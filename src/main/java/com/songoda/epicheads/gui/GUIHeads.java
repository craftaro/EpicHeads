package com.songoda.epicheads.gui;

import com.songoda.core.compatibility.LegacyMaterials;
import com.songoda.core.gui.Gui;
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
    private int page = 0;

    private int maxPage;

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

        updateTitle();
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

        this.maxPage = (int) Math.floor(numHeads / 45.0);

        this.setDefaultItem(null);
        this.setRows(6);
        this.setTitle(name + " (" + numHeads + ") " + plugin.getLocale().getMessage("general.word.page") + " " + (page + 1) + "/" + (maxPage + 1));
        showPage();
    }

    void showPage() {
        List<Head> heads = this.heads.stream().skip(page * 45).limit(45)
                .collect(Collectors.toList());

        if (page - 2 > 0) {
            ItemStack arrow = GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page - 2));
            arrow.setAmount(page - 2);
            setButton(0, arrow,
                    (event) -> {
                        page -= 3;
                        updateTitle();
                    });
        }

        if (page - 1 > 0) {
            ItemStack arrow = GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page - 1));
            arrow.setAmount(page - 1);
            setButton(1, arrow,
                    (event) -> {
                        page -= 2;
                        updateTitle();
                    });
        }

        if (page != 0) {
            ItemStack arrow = GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + page);
            arrow.setAmount(page);
            setButton(2, arrow,
                    (event) -> {
                        page--;
                        updateTitle();
                    });
        }

        setButton(3, GuiUtils.createButtonItem(LegacyMaterials.COMPASS,
                plugin.getLocale().getMessage("gui.heads.search").getMessage()),
                (event) -> doSearch(plugin, event.player));

        ItemStack map = GuiUtils.createButtonItem(LegacyMaterials.MAP,
                plugin.getLocale().getMessage("gui.heads.categories").getMessage());
        map.setAmount(page + 1);
        setButton(4, map, (event) -> plugin.getGuiManager().showGUI(player, new GUIOverview(plugin, event.player)));


        if (heads.size() > 1)
            setButton(5, GuiUtils.createButtonItem(LegacyMaterials.COMPASS,
                    plugin.getLocale().getMessage("gui.heads.refine").getMessage()),
                    (event) -> {
                        plugin.getLocale().getMessage("general.search.refine").sendPrefixedMessage(event.player);
                        ChatPrompt chatPrompt = ChatPrompt.showPrompt(plugin, event.player, promptEvent -> {
                            this.page = 0;
                            this.heads = this.heads.stream().filter(head -> head.getName().toLowerCase()
                                    .contains(promptEvent.getMessage().toLowerCase())).collect(Collectors.toList());
                            if (query == null)
                                this.query = promptEvent.getMessage();
                            else
                                this.query += ", " + promptEvent.getMessage();
                        });
                        chatPrompt.setOnClose(this::updateTitle);
                    });

        if (page != maxPage) {
            ItemStack arrow = GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page + 2));
            arrow.setAmount(page + 2);
            setButton(6, arrow,
                    (event) -> {
                        page++;
                        updateTitle();
                    });
        }

        if (page + 1 < maxPage) {
            ItemStack arrow = GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page + 3));
            arrow.setAmount(page + 3);
            setButton(7, arrow,
                    (event) -> {
                        page += 2;
                        updateTitle();
                    });
        }

        if (page + 2 < maxPage) {
            ItemStack arrow = GuiUtils.createButtonItem(LegacyMaterials.ARROW,
                    ChatColor.RED.toString() + plugin.getLocale().getMessage("general.word.page") + " " + (page + 4));
            arrow.setAmount(page + 4);
            setButton(8, arrow,
                    (event) -> {
                        page += 3;
                        updateTitle();
                    });
        }

        List<String> favorites = plugin.getPlayerManager().getPlayer(player).getFavorites();

        for (int i = 0; i < heads.size(); i++) {
            Head head = heads.get(i);

            if (head.getName() == null) continue;

            boolean free = player.hasPermission("epicheads.bypasscost")
                    || (Settings.FREE_IN_CREATIVE.getBoolean() && player.getGameMode() == GameMode.CREATIVE);

            ItemStack item = head.asItemStack(favorites.contains(head.getURL()), free);

            double cost = Settings.HEAD_COST.getDouble();

            setButton(i + 9, item, (event) -> {
                if (event.clickType == ClickType.SHIFT_LEFT || event.clickType == ClickType.SHIFT_RIGHT) {
                    EPlayer ePlayer = plugin.getPlayerManager().getPlayer(player);
                    if (!ePlayer.getFavorites().contains(head.getURL()))
                        ePlayer.addFavorite(head.getURL());
                    else
                        ePlayer.removeFavorite(head.getURL());
                    updateTitle();
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                meta.setLore(new ArrayList<>());
                item.setItemMeta(meta);


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
                player.getInventory().addItem(item);
            });
        }
    }

    public static void doSearch(EpicHeads plugin, Player player) {
        plugin.getLocale().getMessage("general.search.global").sendPrefixedMessage(player);
        ChatPrompt.showPrompt(plugin, player, event -> {
            List<Head> heads = plugin.getHeadManager().getHeads().stream()
                    .filter(head -> head.getName().toLowerCase().contains(event.getMessage().toLowerCase()))
                    .collect(Collectors.toList());
            Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeads.getInstance(), () ->
                    plugin.getGuiManager().showGUI(player, new GUIHeads(plugin, player, event.getMessage(), QueryTypes.SEARCH, heads)), 0L);
        });
    }

    public enum QueryTypes {
        SEARCH, CATEGORY, FAVORITES, PACK
    }
}
