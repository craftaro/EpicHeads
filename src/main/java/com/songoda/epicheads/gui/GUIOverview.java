package com.songoda.epicheads.gui;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Tag;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.SettingsManager;
import com.songoda.epicheads.utils.gui.AbstractGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

            if (!player.hasPermission("epicheads.category." + tag.getName())) return;

            TagInfo tagInfo = TagInfo.valueOf(tag.getName().toUpperCase());

            createButton(i + 10 + add, Methods.addTexture(new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3),
                    tagInfo.getUrl()),
                    plugin.getLocale().getMessage("gui.overview.headname", tagInfo.getName()),
                    plugin.getLocale().getMessage("gui.overview.headlore", tag.getCount()));

            registerClickable(i + 10 + add, ((player1, inventory1, cursor, slot, type) ->
                    new GUIHeads(plugin, player, null, plugin.getHeadManager().getHeadsByTag(tag))));
        }

        createButton(39, Material.COMPASS, plugin.getLocale().getMessage("gui.overview.search"));


        if (SettingsManager.Setting.DISCORD.getBoolean()) {

            ArrayList<String> lore2 = new ArrayList<>();
            String[] parts2 = plugin.getLocale().getMessage("gui.overview.discordlore").split("\\|");
            for (String line : parts2)
                lore2.add(Methods.formatText(line));

            createButton(41, Methods.addTexture(new ItemStack(Material.PLAYER_HEAD, 1, (byte) 3),
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

        registerClickable(39, ((player1, inventory1, cursor, slot, type) ->
                GUIHeads.doSearch(player1)));

        registerClickable(41, ((player1, inventory1, cursor, slot, type) -> {
            player.sendMessage(Methods.formatText(plugin.getReferences().getPrefix() + "&9https://discord.gg/A9TRJQb"));
            player.closeInventory();
        }));
    }

    @Override
    protected void registerOnCloses() {

    }

    public enum TagInfo {
        ALPHABET("&9&lAlphabet", "9c60da2944a177dd08268fbec04e40812d1d929650be66529b1ee5e1e7eca"),
        HUMANS("&a&lHumans", "b7c224f0453e745c85966025e7767d592feea3ad4c69d2366d94d5e8c9a8c2ce"),
        FOOD("&b&lFood", "f7b9f08ada4e8ba586a04ed2e9e25fe8b9d568a665243f9c603799a7c896736"),
        MISC("&8&lMisc", "f5612dc7b86d71afc1197301c15fd979e9f39e7b1f41d8f1ebdf8115576e2e"),
        ANIMALS("&d&lAnimals", "d8cdd4f285632c25d762ece25f4193b966c2641b15d9bdbc0a113023de76ab"),
        GAMES("&b&lGames", "dba8d8e53d8a5a75770b62cce73db6bab701cc3de4a9b654d213d54af9615"),
        MONSTERS("&c&lMonsters", "68d2183640218ab330ac56d2aab7e29a9790a545f691619e38578ea4a69ae0b6"),
        INTERIOR("&6&lInterior", "ec6d9024fc5412e8e2664123732d2291dfc6bb175f72cf894096f7f313641fd4"),
        BLOCKS("&9&lBlocks", "7788f5ddaf52c5842287b9427a74dac8f0919eb2fdb1b51365ab25eb392c47");

        String name;
        String url;

        TagInfo(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

}
