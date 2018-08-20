package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.oldmenu.Menus;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.oldmenu.Menu;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RemoveMode extends BaseMode {
    
    public RemoveMode(Player player) {
        super(player);
        
        Lang.Menu.Remove.open().send(player);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.REMOVE.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        openInventory(InventoryType.CONFIRM, head);
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Heads.getCache().removeHead(head);
        Heads.getInstance().saveCache();
        
        Lang.Menu.Remove.removed(head.getName()).send(e.getWhoClicked());
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
