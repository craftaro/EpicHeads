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

public class CategoryCostRemoveMode extends BaseMode {

    private final double newCost = Heads.getMainConfig().getDefaultHeadCost();

    public CategoryCostRemoveMode(Player player) {
        super(player);

        Lang.Menu.CategoryCost.openRemove(newCost).send(getPlayer());
    }

    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.CATEGORY_COST_REMOVE.fromType(type);
    }

    public CacheHead getCategoryHead(String category) {
        List<CacheHead> heads = Heads.getCache().getCategoryHeads(category);

        return (heads.size() > 0 ? heads.get(0) : null);
    }

    @Override
    public void onCategorySelect(String category) {
        CacheHead head = this.getCategoryHead(category);

        if(head == null) {
            this.getPlayer().sendMessage(ChatColor.RED + "Invalid category");
            return;
        }

        openInventory(InventoryType.CONFIRM,
                head,
                ArrayUtils.create(new Placeholder("%newcost%", Lang.Currency.format(newCost))));
    }

    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Lang.Menu.CategoryCost.removeCost(head.getCategory(), newCost).send(e.getWhoClicked());

        Heads.getMainConfig().removeCategoryCost(head.getCategory());
    }

    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }

    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {}
    
}
