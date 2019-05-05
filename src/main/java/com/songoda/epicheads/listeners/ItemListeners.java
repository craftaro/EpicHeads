package com.songoda.epicheads.listeners;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
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
    public void itemPickupEvent(EntityPickupItemEvent event) {

        ItemStack item = event.getItem().getItemStack();

        if (item.getType() != Material.PLAYER_HEAD || event.getItem().hasMetadata("EHE")) return;

        event.getItem().removeMetadata("EHE", plugin);

        String url = Methods.getDecodedTexture(item);

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

            event.getItem().getWorld().dropItemNaturally(event.getEntity().getLocation().add(1, 1, 0), itemNew.clone());

            event.getItem().setItemStack(itemNew);
        }
    }

}
