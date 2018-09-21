package com.songoda.epicheads.oldmenu.mode;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.config.oldmenu.Menu;
import com.songoda.epicheads.config.oldmenu.Menus;
import com.songoda.epicheads.oldmenu.ConfirmMenu;
import com.songoda.epicheads.oldmenu.HeadMenu;
import com.songoda.epicheads.oldmenu.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IdMode extends BaseMode {
    
    public IdMode(Player player) {
        super(player);

        player.sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.get.open"));
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.ID.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        e.getWhoClicked().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.id.clicked", head.getName(), head.getId()));
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        // should not be reached
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
