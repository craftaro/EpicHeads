package com.songoda.epicheads.oldmenu;

import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.config.lang.Placeholder;
import com.songoda.epicheads.config.oldmenu.Menu;
import com.songoda.epicheads.oldmenu.mode.InvMode;
import com.songoda.epicheads.util.ArrayUtils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
