package nl.marido.deluxeheads.oldmenu.mode;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.md_5.bungee.api.ChatColor;
import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.config.lang.Placeholder;
import nl.marido.deluxeheads.config.oldmenu.Menu;
import nl.marido.deluxeheads.config.oldmenu.Menus;
import nl.marido.deluxeheads.oldmenu.ConfirmMenu;
import nl.marido.deluxeheads.oldmenu.HeadMenu;
import nl.marido.deluxeheads.oldmenu.InventoryType;
import nl.marido.deluxeheads.util.ArrayUtils;

public class CategoryCostRemoveMode extends BaseMode {

	private final double newCost = DeluxeHeads.getMainConfig().getDefaultHeadCost();

	public CategoryCostRemoveMode(Player player) {
		super(player);

		Lang.Menu.CategoryCost.openRemove(newCost).send(getPlayer());
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.CATEGORY_COST_REMOVE.fromType(type);
	}

	public CacheHead getCategoryHead(String category) {
		List<CacheHead> heads = DeluxeHeads.getCache().getCategoryHeads(category);

		return (heads.size() > 0 ? heads.get(0) : null);
	}

	@Override
	public void onCategorySelect(String category) {
		CacheHead head = this.getCategoryHead(category);

		if (head == null) {
			this.getPlayer().sendMessage(ChatColor.RED + "Invalid category");
			return;
		}

		openInventory(InventoryType.CONFIRM, head, ArrayUtils.create(new Placeholder("%newcost%", Lang.Currency.format(newCost))));
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		Lang.Menu.CategoryCost.removeCost(head.getCategory(), newCost).send(e.getWhoClicked());

		DeluxeHeads.getMainConfig().removeCategoryCost(head.getCategory());
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
	}

}
