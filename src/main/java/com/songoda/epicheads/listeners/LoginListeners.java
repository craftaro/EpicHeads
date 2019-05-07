package com.songoda.epicheads.listeners;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.HeadManager;
import com.songoda.epicheads.head.Tag;
import com.songoda.epicheads.utils.Methods;
import com.songoda.epicheads.utils.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;

public class LoginListeners implements Listener {

    private final EpicHeads plugin;

    public LoginListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void loginEvent(PlayerLoginEvent event) {
        HeadManager headManager = plugin.getHeadManager();

        ItemStack item = new ItemStack(plugin.isServerVersionAtLeast(ServerVersion.V1_13)
                ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"), 1, (byte) 3);

        Player player = event.getPlayer();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(player);
        item.setItemMeta(meta);

        String url = Methods.getDecodedTexture(item);

        String tagStr = "Player Heads";

        Optional<Tag> tagOptional = headManager.getTags()
                .stream().filter(t -> t.getName().equalsIgnoreCase(tagStr)).findFirst();

        Tag tag = tagOptional.orElseGet(() -> new Tag(tagStr));

        if (!tagOptional.isPresent())
            headManager.addTag(tag);

        Optional<Head> optional = headManager.getLocalHeads().stream()
                .filter(h -> h.getName().equalsIgnoreCase(event.getPlayer().getName())).findFirst();

        int id = headManager.getNextLocalId();

        if (optional.isPresent()) {
            Head head = optional.get();
            id = head.getId();
            headManager.removeLocalHead(head);
        }

        headManager.addLocalHeads(new Head(id, player.getName(), url, tag, (byte) 0));

    }

}
