package com.songoda.epicheads.oldmenu.mode;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.config.lang.Placeholder;
import com.songoda.epicheads.config.oldmenu.Menu;
import com.songoda.epicheads.config.oldmenu.Menus;
import com.songoda.epicheads.oldmenu.ConfirmMenu;
import com.songoda.epicheads.oldmenu.HeadMenu;
import com.songoda.epicheads.oldmenu.InventoryType;
import com.songoda.epicheads.util.ArrayUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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

		getPlayer().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.rename.open", name));
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
		e.getWhoClicked().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.rename.renamed", head.getName(), name));

		head.setName(name);
		EpicHeads.getInstance().saveCache();
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
