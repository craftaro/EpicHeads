package net.sothatsit.heads.oldmenu;

import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.oldmenu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.oldmenu.mode.InvMode;
import net.sothatsit.heads.oldmenu.mode.SearchMode;
import net.sothatsit.heads.util.ArrayUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HeadMenu extends AbstractModedInventory {
    
    private String category;
    private List<CacheHead> heads;
    private int page;
    
    public HeadMenu(InvMode mode, String category, List<CacheHead> heads) {
        super(InventoryType.HEADS, 54, new Placeholder[] { new Placeholder("%category%", category) }, mode);
        
        this.category = category;
        this.heads = heads;
        this.page = 0;
        
        recreate();
    }
    
    @Override
    public void recreate() {
        Menu menu = getMenu();
        Player player = getInvMode().getPlayer();
        
        int maxPage = (int) Math.ceil((double) heads.size() / 45d);
        
        page += maxPage;
        page %= maxPage;
        
        Placeholder[] placeholders = {
                new Placeholder("%category%", category),
                new Placeholder("%page%", Integer.toString(page))
        };
        
        ItemStack[] contents = new ItemStack[54];
        
        ItemStack glass = menu.getItemStack("filler", placeholders);
        for (int i = 45; i < 54; i++) {
            contents[i] = glass.clone();
        }
        
        if (page != 0) {
            contents[45] = menu.getItemStack("backwards", placeholders);
        }
        
        if (page != maxPage - 1) {
            contents[53] = menu.getItemStack("forwards", placeholders);
        }
        
        if(!(getInvMode() instanceof SearchMode)) {
            contents[49] = menu.getItemStack("back", placeholders);
        }
        
        for (int i = page * 45; i < (page + 1) * 45; i++) {
            int index = i % 45;
            
            if (i < heads.size()) {
                CacheHead head = heads.get(i);

                String id = "head";

                if(getInvMode() instanceof SearchMode) {
                    id = ((SearchMode) getInvMode()).getHeadId(head);
                }

                placeholders[0] = new Placeholder("%category%", head.getCategory());
                Placeholder[] holders = ArrayUtils.append(placeholders, head.getPlaceholders(player));

                contents[index] = head.addTexture(menu.getItemStack(id, holders));
            }
        }
        
        getInventory().setContents(contents);
    }
    
    public void backwardsPage() {
        if (page > 0) {
            page--;
            
            recreate();
        }
    }
    
    public void forwardsPage() {
        if (page < getMaxPage() - 1) {
            page++;
            
            recreate();
        }
    }
    
    public int getPage() {
        return page;
    }
    
    public int getMaxPage() {
        return (int) Math.ceil((double) heads.size() / 45d);
    }
    
    public boolean isHead(int slot) {
        return slot < 45 && (page * 45 + slot) < heads.size();
    }
    
    public CacheHead getHead(int slot) {
        return (isHead(slot) ? heads.get(page * 45 + slot) : null);
    }
    
    public boolean isToolBar(int slot) {
        return slot >= 45;
    }
    
    public boolean isBackwards(int slot) {
        return page > 0 && slot == 45;
    }
    
    public boolean isForwards(int slot) {
        return page < getMaxPage() - 1 && slot == 53;
    }
    
    public boolean isBackToMenu(int slot) {
        return slot == 49;
    }
    
    public boolean handleToolbar(int slot) {
        if (!isToolBar(slot)) {
            return false;
        }
        
        if (isBackwards(slot)) {
            backwardsPage();
        } else if (isForwards(slot)) {
            forwardsPage();
        } else if (isBackToMenu(slot) && !(getInvMode() instanceof SearchMode)) {
            getInvMode().openInventory(InventoryType.CATEGORY);
        }
        
        return true;
    }
    
}
