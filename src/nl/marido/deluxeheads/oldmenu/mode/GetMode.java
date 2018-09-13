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

		if (!DeluxeHeads.getInstance().chargeForHead(player, head))
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
		if (getPlayer().hasPermission("deluxeheads.category." + category.toLowerCase().replace(' ', '_'))) {
			return true;
		} else {
			Lang.Menu.Get.categoryPermission(category).send(getPlayer());
			return false;
		}
	}

}
