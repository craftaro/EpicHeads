package nl.marido.heads.oldmenu.mode;

import net.md_5.bungee.api.ChatColor;
import nl.marido.heads.Heads;
import nl.marido.heads.cache.CacheHead;
import nl.marido.heads.config.lang.Lang;
import nl.marido.heads.config.lang.Placeholder;
import nl.marido.heads.config.oldmenu.Menu;
import nl.marido.heads.config.oldmenu.Menus;
import nl.marido.heads.oldmenu.ConfirmMenu;
import nl.marido.heads.oldmenu.HeadMenu;
import nl.marido.heads.oldmenu.InventoryType;
import nl.marido.heads.util.ArrayUtils;

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
