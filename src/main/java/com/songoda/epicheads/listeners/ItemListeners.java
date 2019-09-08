package com.songoda.epicheads.listeners;

import com.songoda.core.compatibility.LegacyMaterials;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Optional;

public class ItemListeners implements Listener {

    private final EpicHeads plugin;

    public ItemListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void itemSpawnEvent(ItemSpawnEvent event) {
        ItemStack item = event.getEntity().getItemStack();

        if (!LegacyMaterials.PLAYER_HEAD.matches(item)) return;

        String encodededStr = Methods.getEncodedTexture(item);

        if (encodededStr == null) return;

        String url = Methods.getDecodedTexture(encodededStr);

        if (url == null) return;
        Optional<Head> optional = plugin.getHeadManager().getHeads().stream()
                .filter(head -> url.equals(head.getURL())).findFirst();

        if (optional.isPresent()) {
            ItemStack itemNew = optional.get().asItemStack();

            ItemMeta meta = itemNew.getItemMeta();
            meta.setLore(new ArrayList<>());
            item.setItemMeta(meta);
        }
    }

}
