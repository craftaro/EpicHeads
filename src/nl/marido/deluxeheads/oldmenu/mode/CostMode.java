package nl.marido.deluxeheads.oldmenu.mode;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
		openInventory(InventoryType.CONFIRM, head, ArrayUtils.create(new Placeholder("%newcost%", Lang.Currency.format(cost))));
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		Lang.Menu.Cost.setCost(head.getName(), cost).send(e.getWhoClicked());

		head.setCost(cost);
		DeluxeHeads.getInstance().saveCache();
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
