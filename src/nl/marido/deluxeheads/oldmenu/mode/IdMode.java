package nl.marido.deluxeheads.oldmenu.mode;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.config.oldmenu.Menu;
import nl.marido.deluxeheads.config.oldmenu.Menus;
import nl.marido.deluxeheads.oldmenu.ConfirmMenu;
import nl.marido.deluxeheads.oldmenu.HeadMenu;
import nl.marido.deluxeheads.oldmenu.InventoryType;

public class IdMode extends BaseMode {
    
    public IdMode(Player player) {
        super(player);
        
        Lang.Menu.Get.open().send(player);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.ID.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        Lang.Menu.Id.clicked(head.getName(), head.getId()).send(e.getWhoClicked());
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
