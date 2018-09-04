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
		List<CacheHead> heads = DeluxeHeads.getCache().getCategoryHeads(category);

		return (heads.size() > 0 ? heads.get(0) : null);
	}

	@Override
	public void onCategorySelect(String category) {
		CacheHead head = getCategoryHead(category);

		if (head == null) {
			getPlayer().sendMessage(ChatColor.RED + "Invalid category");
			return;
		}

		openInventory(InventoryType.CONFIRM, head, ArrayUtils.create(new Placeholder("%newcost%", Lang.Currency.format(cost))));
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		Lang.Menu.CategoryCost.setCost(head.getCategory(), cost).send(e.getWhoClicked());

		DeluxeHeads.getMainConfig().setCategoryCost(head.getCategory(), cost);
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
	}

}
