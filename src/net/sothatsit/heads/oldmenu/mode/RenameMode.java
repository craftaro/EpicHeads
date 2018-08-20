package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.oldmenu.Menus;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.oldmenu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;
import net.sothatsit.heads.util.ArrayUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RenameMode extends BaseMode {
    
    private String name = null;
    
    public RenameMode(Player player) {
        super(player);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        
        Lang.Menu.Rename.open(name).send(getPlayer());
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.RENAME.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        openInventory(InventoryType.CONFIRM,
                head,
                ArrayUtils.create(new Placeholder("%newname%", name)));
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Lang.Menu.Rename.renamed(head.getName(), name).send(e.getWhoClicked());
        
        head.setName(name);
        Heads.getInstance().saveCache();
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
