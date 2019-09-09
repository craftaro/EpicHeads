package com.songoda.epicheads.listeners;

import com.songoda.core.utils.ItemUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.HeadManager;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Optional;

public class LoginListeners implements Listener {

    private final EpicHeads plugin;

    public LoginListeners(EpicHeads plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void loginEvent(PlayerLoginEvent event) {
        HeadManager headManager = plugin.getHeadManager();

        Player player = event.getPlayer();

        String encodededStr = ItemUtils.getSkullTexture(player);

        if (encodededStr == null) return;

        String url = ItemUtils.getDecodedTexture(encodededStr);

        String tagStr = "Player Heads";

        Optional<Category> tagOptional = headManager.getCategories()
                .stream().filter(t -> t.getName().equalsIgnoreCase(tagStr)).findFirst();

        Category tag = tagOptional.orElseGet(() -> new Category(tagStr));

        if (!tagOptional.isPresent())
            headManager.addCategory(tag);

        Optional<Head> optional = headManager.getLocalHeads().stream()
                .filter(h -> h.getName().equalsIgnoreCase(event.getPlayer().getName())).findFirst();

        int id = headManager.getNextLocalId();

        if (optional.isPresent()) {
            Head head = optional.get();
            id = head.getId();
            headManager.removeLocalHead(head);
        }

        headManager.addLocalHeads(new Head(id, player.getName(), url, tag, null, (byte) 0));

    }

}
