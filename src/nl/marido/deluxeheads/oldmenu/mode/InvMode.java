package nl.marido.deluxeheads.oldmenu.mode;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import nl.marido.deluxeheads.config.oldmenu.Menu;
import nl.marido.deluxeheads.oldmenu.AbstractModedInventory;
import nl.marido.deluxeheads.oldmenu.InventoryType;

public abstract class InvMode {
    
    private AbstractModedInventory inventory;
    private Player player;
    
    public InvMode(Player player, InventoryType type, Object... arguments) {
        this.player = player;
        
        openInventory(type, arguments);
    }
    
    public Player getPlayer() {
        return this.player;
    }

    @SuppressWarnings("unchecked")
    public <T extends InvMode> T asType(Class<T> clazz) {
        return (T) this;
    }
    
    public AbstractModedInventory getInventory() {
        return this.inventory;
    }

    @SuppressWarnings("unchecked")
    public <T extends AbstractModedInventory> T getInventory(Class<T> clazz) {
        return (T) this.inventory;
    }
    
    public void setInventory(AbstractModedInventory inventory) {
        this.inventory = inventory;
        
        this.player.openInventory(inventory.getInventory());
    }
    
    public void openInventory(InventoryType type, Object... arguments) {
        setInventory(type.createMenu(this, arguments));
    }
    
    public void closeInventory() {
        player.closeInventory();
    }
    
    public abstract Menu getMenu(InventoryType type);
    
    public abstract void onClick(InventoryClickEvent e, InventoryType type);
    
}
