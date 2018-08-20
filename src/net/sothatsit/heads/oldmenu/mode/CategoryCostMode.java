package net.sothatsit.heads.oldmenu.mode;

import net.md_5.bungee.api.ChatColor;
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

import java.util.List;

public class CategoryCostMode extends BaseMode {

    private Double cost = null;

    public CategoryCostMode(Player player) {
        super(player);
    }

    public void setCost(Double cost) {
        this.cost = cost;
        
        Lang.Menu.CategoryCost.open(cost).send(getPlayer());
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.CATEGORY_COST.fromType(type);
    }

    public CacheHead getCategoryHead(String category) {
        List<CacheHead> heads = Heads.getCache().getCategoryHeads(category);

        return (heads.size() > 0 ? heads.get(0) : null);
    }

    @Override
    public void onCategorySelect(String category) {
        CacheHead head = getCategoryHead(category);

        if(head == null) {
            getPlayer().sendMessage(ChatColor.RED + "Invalid category");
            return;
        }

        openInventory(InventoryType.CONFIRM,
                head,
                ArrayUtils.create(new Placeholder("%newcost%", Lang.Currency.format(cost))));
    }

    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Lang.Menu.CategoryCost.setCost(head.getCategory(), cost).send(e.getWhoClicked());

        Heads.getMainConfig().setCategoryCost(head.getCategory(), cost);
    }

    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }

    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {}
    
}
