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

public class GetMode extends BaseMode {

	public GetMode(Player player) {
		super(player);

		player.sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.get.open"));
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.GET.fromType(type);
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
		Player player = getPlayer();

		if (!EpicHeads.getInstance().chargeForHead(player, head))
			return;

		//Lang.Menu.Get.added(head.getName()).send(player); ToDo: What was this?

		player.getInventory().addItem(head.getItemStack());
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		// should not be reached
	}

	@Override
	public boolean canOpenCategory(String category) {
		if (getPlayer().hasPermission("EpicHeads.category." + category.toLowerCase().replace(' ', '_'))) {
			return true;
		} else {
			getPlayer().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.search.nopermission", category));
			return false;
		}
	}

}
