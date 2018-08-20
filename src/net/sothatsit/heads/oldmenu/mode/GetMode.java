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

public class GetMode extends BaseMode {
    
    public GetMode(Player player) {
        super(player);
        
        Lang.Menu.Get.open().send(player);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.GET.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        Player player = getPlayer();

        if(!Heads.getInstance().chargeForHead(player, head))
            return;
        
        Lang.Menu.Get.added(head.getName()).send(player);

        player.getInventory().addItem(head.getItemStack());
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        // should not be reached
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        if (getPlayer().hasPermission("heads.category." + category.toLowerCase().replace(' ', '_'))) {
            return true;
        } else {
            Lang.Menu.Get.categoryPermission(category).send(getPlayer());
            return false;
        }
    }
    
}
