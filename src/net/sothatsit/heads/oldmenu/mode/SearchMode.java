package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.oldmenu.Menus;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.oldmenu.Menu;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.economy.Economy;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class SearchMode extends BaseMode {

    public SearchMode(Player player, List<CacheHead> heads) {
        super(player, InventoryType.HEADS, "Search", heads);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.SEARCH.heads();
    }

    public String getHeadId(CacheHead head) {
        if(!getPlayer().hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
            return "head-no-perms";
        } else {
            return (head.hasCost() && Heads.getMainConfig().isEconomyEnabled() ? "head-cost" : "head");
        }
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        Player player = getPlayer();

        if (!player.hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
            Lang.Menu.Search.categoryPermission(head.getCategory()).send(getPlayer());
            return;
        }

        if(!Heads.getInstance().chargeForHead(player, head))
            return;
        
        Lang.Menu.Search.added(head.getName()).send(player);

        player.getInventory().addItem(head.getItemStack());
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
