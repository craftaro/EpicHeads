package nl.marido.deluxeheads.oldmenu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.config.lang.Placeholder;
import nl.marido.deluxeheads.config.oldmenu.Menu;
import nl.marido.deluxeheads.oldmenu.mode.InvMode;
import nl.marido.deluxeheads.util.ArrayUtils;

public class ConfirmMenu extends AbstractModedInventory {
    
    private CacheHead subject;
    private Placeholder[] placeholders;
    
    public ConfirmMenu(InvMode mode, CacheHead subject) {
        this(mode, subject, new Placeholder[0]);
    }
    
    public ConfirmMenu(InvMode mode, CacheHead subject, Placeholder[] placeholders) {
        super(InventoryType.CONFIRM, 45,
                ArrayUtils.append(placeholders, subject.getPlaceholders(mode.getPlayer())),
                mode);
        
        this.subject = subject;
        this.placeholders = ArrayUtils.append(placeholders, subject.getPlaceholders(mode.getPlayer()));
        
        recreate();
    }
    
    @Override
    public void recreate() {
        Inventory inv = getInventory();
        Menu menu = getMenu();
        
        ItemStack[] contents = new ItemStack[inv.getSize()];
        
        contents[13] = subject.addTexture(menu.getItemStack("head", placeholders));
        contents[29] = menu.getItemStack("accept", placeholders);
        contents[33] = menu.getItemStack("deny", placeholders);
        
        inv.setContents(contents);
    }
    
    public CacheHead getSubject() {
        return subject;
    }
    
    public boolean isConfirm(int slot) {
        return slot == 29;
    }
    
    public boolean isDeny(int slot) {
        return slot == 33;
    }
}
