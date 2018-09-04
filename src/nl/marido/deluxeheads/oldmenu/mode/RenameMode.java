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

public class RenameMode extends BaseMode {

	private String name = null;

	public RenameMode(Player player) {
		super(player);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;

		Lang.Menu.Rename.open(name).send(getPlayer());
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.RENAME.fromType(type);
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
		openInventory(InventoryType.CONFIRM, head, ArrayUtils.create(new Placeholder("%newname%", name)));
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		Lang.Menu.Rename.renamed(head.getName(), name).send(e.getWhoClicked());

		head.setName(name);
		DeluxeHeads.getInstance().saveCache();
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
