package com.songoda.epicheads.oldmenu.mode;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.config.oldmenu.Menu;
import com.songoda.epicheads.config.oldmenu.Menus;
import com.songoda.epicheads.oldmenu.ConfirmMenu;
import com.songoda.epicheads.oldmenu.HeadMenu;
import com.songoda.epicheads.oldmenu.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RemoveMode extends BaseMode {

	public RemoveMode(Player player) {
		super(player);

		player.sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.get.open"));
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
		EpicHeads.getCache().removeHead(head);
		EpicHeads.getInstance().saveCache();


		e.getWhoClicked().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.remove.removed", head.getName()));
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
