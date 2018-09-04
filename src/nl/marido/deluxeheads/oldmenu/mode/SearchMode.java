package nl.marido.deluxeheads.oldmenu.mode;

import java.util.List;

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

public class SearchMode extends BaseMode {

	public SearchMode(Player player, List<CacheHead> heads) {
		super(player, InventoryType.HEADS, "Search", heads);
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.SEARCH.heads();
	}

	public String getHeadId(CacheHead head) {
		if (!getPlayer().hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
			return "head-no-perms";
		} else {
			return (head.hasCost() && DeluxeHeads.getMainConfig().isEconomyEnabled() ? "head-cost" : "head");
		}
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
		Player player = getPlayer();

		if (!player.hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
			Lang.Menu.Search.categoryPermission(head.getCategory()).send(getPlayer());
			return;
		}

		if (!DeluxeHeads.getInstance().chargeForHead(player, head))
			return;

		Lang.Menu.Search.added(head.getName()).send(player);

		player.getInventory().addItem(head.getItemStack());
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		// should not be reached
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
