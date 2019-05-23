package com.songoda.epicheads.listeners;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.utils.HeadType;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import com.songoda.epicheads.utils.settings.Setting;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Optional;

public class DeathListeners implements Listener {

    private final EpicHeads plugin;

    public DeathListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {

        int ch = Integer.parseInt(Setting.DROP_CHANCE.getString().replace("%", ""));
        double rand = Math.random() * 100;
        if (rand - ch < 0 || ch == 100) {

            ItemStack itemNew;
            if (event.getEntity() instanceof Player) {
                if (!Setting.DROP_PLAYER_HEADS.getBoolean()) return;

                String encodededStr = Methods.getEncodedTexture((Player) event.getEntity());

                if (encodededStr == null) {
                    itemNew = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                            ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3);

                    ItemMeta meta = itemNew.getItemMeta();
                    meta.setDisplayName(Methods.formatText("&9" + ((Player) event.getEntity()).getDisplayName()));
                    itemNew.setItemMeta(meta);
                } else {

                    String url = Methods.getDecodedTexture(encodededStr);

                    Optional<Head> optional = plugin.getHeadManager().getHeads().stream()
                            .filter(head -> url.equals(head.getURL())).findFirst();

                    itemNew = optional.get().asItemStack();
                }
            } else {
                if (!Setting.DROP_MOB_HEADS.getBoolean() || event.getEntity() instanceof ArmorStand) return;

                Head head = new Head(-1, null, HeadType.valueOf(event.getEntity().getType().name()).getUrl(), null, null, (byte) 0);
                itemNew = head.asItemStack();
            }

            ItemMeta meta = itemNew.getItemMeta();
            meta.setLore(new ArrayList<>());
            itemNew.setItemMeta(meta);

            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemNew);

        }
    }
}
