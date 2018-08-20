package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.oldmenu.CategorySelectMenu;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class BaseMode extends InvMode {
    
    public BaseMode(Player player) {
        super(player, InventoryType.CATEGORY);
    }

    public BaseMode(Player player, InventoryType type, Object... args) {
        super(player, type, args);
    }
    
    @Override
    public void onClick(InventoryClickEvent e, InventoryType type) {
        e.setCancelled(true);
        
        if (e.getClickedInventory() != null && e.getClickedInventory().equals(getInventory().getInventory())) {
            switch (type) {
                case CATEGORY:
                    onCategoryClick(e);
                    break;
                case HEADS:
                    onHeadsClick(e);
                    break;
                case CONFIRM:
                    onConfirmClick(e);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onCategoryClick(InventoryClickEvent e) {
        if(e.getCurrentItem() == null)
            return;

        CategorySelectMenu menu = getInventory(CategorySelectMenu.class);

        String category = menu.getCategory(e.getRawSlot());

        if(category != null) {
            this.onCategorySelect(category);
        }
    }

    public void onCategorySelect(String category) {
        if (!canOpenCategory(category)) {
            return;
        }

        CategorySelectMenu menu = getInventory(CategorySelectMenu.class);

        openInventory(InventoryType.HEADS, category, menu.getHeads(category));
    }

    public abstract boolean canOpenCategory(String category);
    
    public void onHeadsClick(InventoryClickEvent e) {
        HeadMenu menu = getInventory(HeadMenu.class);
        
        int slot = e.getRawSlot();
        
        if (!menu.handleToolbar(slot)) {
            CacheHead head = menu.getHead(slot);
            
            if (head != null) {
                onHeadSelect(e, menu, head);
            }
        }
    }
    
    public abstract void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head);
    
    public void onConfirmClick(InventoryClickEvent e) {
        ConfirmMenu menu = getInventory(ConfirmMenu.class);
        
        if (menu.isConfirm(e.getRawSlot())) {
            onConfirm(e, menu, menu.getSubject());
            closeInventory();
        }
        
        if (menu.isDeny(e.getRawSlot())) {
            closeInventory();
        }
    }
    
    public abstract void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head);
    
}
