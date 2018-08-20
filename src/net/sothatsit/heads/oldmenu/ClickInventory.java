package net.sothatsit.heads.oldmenu;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface ClickInventory extends InventoryHolder {
    
    public void onClick(InventoryClickEvent e);
    
    public InventoryType getType();
    
}
