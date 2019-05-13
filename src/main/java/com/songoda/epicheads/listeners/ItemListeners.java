package com.songoda.epicheads.listeners;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Optional;

public class ItemListeners implements Listener {

    private final EpicHeads plugin;

    public ItemListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void itemPickupEvent(PlayerPickupItemEvent event) {

        ItemStack item = event.getItem().getItemStack();

        if (item.getType() != (plugin.isServerVersionAtLeast(ServerVersion.V1_13) ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"))
                || event.getItem().hasMetadata("EHE")) return;

        event.getItem().removeMetadata("EHE", plugin);

        String encodededStr = Methods.getEncodedTexture(item);

        if (encodededStr == null) return;

        String url = Methods.getDecodedTexture(encodededStr);

        if (url == null) return;
        Optional<Head> optional = plugin.getHeadManager().getHeads().stream()
                .filter(head -> url.equals(head.getURL())).findFirst();

        if (optional.isPresent()) {
            event.setCancelled(true);
            event.getItem().setMetadata("EHE", new FixedMetadataValue(plugin, true));

            ItemStack itemNew = optional.get().asItemStack();
            itemNew.setAmount(item.getAmount());

            ItemMeta meta = itemNew.getItemMeta();
            meta.setLore(new ArrayList<>());
            itemNew.setItemMeta(meta);

            event.getItem().setItemStack(itemNew);
        }
    }

}
