package nl.marido.heads.oldmenu.mode;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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

public class CostMode extends BaseMode {
    
    private Double cost = null;
    
    public CostMode(Player player) {
        super(player);
    }
    
    public Double getCost() {
        return cost;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
        
        Lang.Menu.Cost.open(cost).send(getPlayer());
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.COST.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        openInventory(InventoryType.CONFIRM,
                head,
                ArrayUtils.create(new Placeholder("%newcost%", Lang.Currency.format(cost))));
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Lang.Menu.Cost.setCost(head.getName(), cost).send(e.getWhoClicked());
        
        head.setCost(cost);
        Heads.getInstance().saveCache();
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
