package nl.marido.heads.oldmenu.mode;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import nl.marido.heads.Heads;
import nl.marido.heads.cache.CacheHead;
import nl.marido.heads.config.lang.Lang;
import nl.marido.heads.config.oldmenu.Menu;
import nl.marido.heads.config.oldmenu.Menus;
import nl.marido.heads.oldmenu.ConfirmMenu;
import nl.marido.heads.oldmenu.HeadMenu;
import nl.marido.heads.oldmenu.InventoryType;

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
