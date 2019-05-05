package com.songoda.epicheads.gui;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.Tag;
import com.songoda.epicheads.players.EPlayer;
import com.songoda.epicheads.utils.AbstractChatConfirm;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GUIHeads extends AbstractGUI {

    private final EpicHeads plugin;

    private List<Head> heads;
    private int page = 0;

    private int maxPage;

    private String query;

    public GUIHeads(EpicHeads plugin, Player player, String query, List<Head> heads) {
        super(player);
        this.plugin = plugin;
        this.query = query;

        List<Integer> favorites = plugin.getPlayerManager().getPlayer(player).getFavorites();
        this.heads = heads.stream()
                .sorted(Comparator.comparing(Head::getName))
                .sorted(Comparator.comparingInt(head -> (head.getStaffPicked() == 1 ? 0 : 1)))
                .sorted(Comparator.comparingInt(head -> (favorites.contains(head.getId()) ? 0 : 1)))
                .collect(Collectors.toList());

        updateTitle();
    }

    private void updateTitle() {


        int numHeads = this.heads.size();
        if (numHeads == 0) {
            player.sendMessage("No heads found.");
            return;
        }
        Tag tag = heads.get(0).getTag();

        this.maxPage = (int) Math.floor(numHeads / 45.0);
        init((query != null ? "Query: " + query : tag.getName()) + " (" + numHeads + ") Page " + (page + 1) + "/" + (maxPage + 1), 54);
        constructGUI();
    }

    @Override
    protected void constructGUI() {
        resetClickables();
        registerClickables();
        List<Head> heads = this.heads.stream().skip(page * 45).limit(45)
                .collect(Collectors.toList());

        if (page - 2 > 0) {
            createButton(0, Material.ARROW, "Back");
            registerClickable(0, ((player1, inventory1, cursor, slot, type) -> {
                page -= 3;
                updateTitle();
            }));
            inventory.getItem(0).setAmount(page - 2);
        }

        if (page - 1 > 0) {
            createButton(1, Material.ARROW, "Back");
            registerClickable(1, ((player1, inventory1, cursor, slot, type) -> {
                page -= 2;
                updateTitle();
            }));
            inventory.getItem(1).setAmount(page - 1);
        }

        if (page != 0) {
            createButton(2, Material.ARROW, "Back");
            registerClickable(2, ((player1, inventory1, cursor, slot, type) -> {
                page--;
                updateTitle();
            }));
            inventory.getItem(2).setAmount(page);
        }

        createButton(3, Material.COMPASS, "Create Search");

        createButton(4, Material.MAP, "Back To Categories");
        inventory.getItem(4).setAmount(page + 1);

        if (heads.size() > 1)
            createButton(5, Material.COMPASS, "Refine Search");

        if (page != maxPage) {
            createButton(6, Material.ARROW, "Next");
            registerClickable(6, ((player1, inventory1, cursor, slot, type) -> {
                page++;
                updateTitle();
            }));
            inventory.getItem(6).setAmount(page + 2);
        }

        if (page + 1 < maxPage) {
            createButton(7, Material.ARROW, "Next");
            registerClickable(7, ((player1, inventory1, cursor, slot, type) -> {
                page += 2;
                updateTitle();
            }));
            inventory.getItem(7).setAmount(page + 3);
        }

        if (page + 2 < maxPage) {
            createButton(8, Material.ARROW, "Next");
            registerClickable(8, ((player1, inventory1, cursor, slot, type) -> {
                page += 3;
                updateTitle();
            }));
            inventory.getItem(8).setAmount(page + 4);
        }

        List<Integer> favorites = plugin.getPlayerManager().getPlayer(player).getFavorites();

        for (int i = 0; i < heads.size(); i++) {
            Head head = heads.get(i);

            if (head.getName() == null) continue;

            ItemStack item = head.asItemStack(favorites.contains(head.getId()));

            inventory.setItem(i + 9, item);

            registerClickable(i + 9, ((player1, inventory1, cursor, slot, type) -> {
                if (type == ClickType.SHIFT_LEFT || type == ClickType.SHIFT_RIGHT) {
                    EPlayer ePlayer = plugin.getPlayerManager().getPlayer(player);
                    if (!ePlayer.getFavorites().contains(head.getId()))
                        ePlayer.addFavorite(head.getId());
                    else
                        ePlayer.removeFavorite(head.getId());
                    updateTitle();
                    return;
                }

                ItemMeta meta = item.getItemMeta();
                meta.setLore(new ArrayList<>());
                item.setItemMeta(meta);

                player.getInventory().addItem(item);
            }));
        }
    }

    @Override
    protected void registerClickables() {
        registerClickable(4, ((player1, inventory1, cursor, slot, type) ->
                new GUIOverview(plugin, player)));

        registerClickable(3, ((player1, inventory1, cursor, slot, type) ->
                doSearch(player1)));

        if (heads.size() > 1) {
            registerClickable(5, ((player1, inventory1, cursor, slot, type) -> {

                player.sendMessage("Refine your search...");
                AbstractChatConfirm abstractChatConfirm = new AbstractChatConfirm(player, event -> {
                    this.page = 0;
                    this.heads = this.heads.stream().filter(head -> head.getName().toLowerCase()
                            .contains(event.getMessage().toLowerCase())).collect(Collectors.toList());
                    if (query == null)
                        this.query = event.getMessage();
                    else
                        this.query += ", " + event.getMessage();
                });
                abstractChatConfirm.setOnClose(this::updateTitle);
            }));
        }
    }

    @Override
    protected void registerOnCloses() {

    }

    public static void doSearch(Player player) {
        player.sendMessage("Enter your query...");
        new AbstractChatConfirm(player, event -> {
            List<Head> heads = EpicHeads.getInstance().getHeadManager().getHeads().stream()
                    .filter(head -> head.getName().toLowerCase().contains(event.getMessage().toLowerCase()))
                    .collect(Collectors.toList());
            Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeads.getInstance(), () ->
                    new GUIHeads(EpicHeads.getInstance(), player, event.getMessage(), heads), 0L);
        });
    }
}
