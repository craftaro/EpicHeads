package nl.marido.deluxeheads.oldmenu.mode;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.config.oldmenu.Menu;
import nl.marido.deluxeheads.config.oldmenu.Menus;
import nl.marido.deluxeheads.oldmenu.ConfirmMenu;
import nl.marido.deluxeheads.oldmenu.HeadMenu;
import nl.marido.deluxeheads.oldmenu.InventoryType;

public class RemoveMode extends BaseMode {

	public RemoveMode(Player player) {
		super(player);

		Lang.Menu.Remove.open().send(player);
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.REMOVE.fromType(type);
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
		openInventory(InventoryType.CONFIRM, head);
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		DeluxeHeads.getCache().removeHead(head);
		DeluxeHeads.getInstance().saveCache();

		Lang.Menu.Remove.removed(head.getName()).send(e.getWhoClicked());
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
