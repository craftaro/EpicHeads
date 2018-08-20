package net.sothatsit.heads.oldmenu;

import net.sothatsit.heads.config.oldmenu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.oldmenu.mode.InvMode;
import org.bukkit.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public abstract class AbstractModedInventory implements ClickInventory {
    
    private InventoryType type;
    private Inventory inventory;
    private InvMode mode;
    private Menu menu;
    
    public AbstractModedInventory(InventoryType type, InvMode mode) {
        this.type = type;
        this.inventory = null;
        this.mode = mode;
        this.menu = mode.getMenu(type);
    }
    
    public AbstractModedInventory(InventoryType type, int size, Placeholder[] titlePlaceholders, InvMode mode) {
        this.type = type;
        this.mode = mode;
        this.menu = mode.getMenu(type);
        this.inventory = Bukkit.createInventory(this, size, menu.getTitle(titlePlaceholders));
    }
    
    public AbstractModedInventory(InventoryType type, Inventory inventory, InvMode mode) {
        this.type = type;
        this.inventory = inventory;
        this.mode = mode;
        this.menu = mode.getMenu(type);
    }
    
    @Override
    public InventoryType getType() {
        return type;
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    public InvMode getInvMode() {
        return mode;
    }
    
    public Menu getMenu() {
        return menu;
    }
    
    @Override
    public void onClick(InventoryClickEvent e) {
        mode.onClick(e, type);
    }
    
    public abstract void recreate();
    
    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
}
