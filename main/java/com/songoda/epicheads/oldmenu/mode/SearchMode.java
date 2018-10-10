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

import java.util.List;

public class SearchMode extends BaseMode {

	public SearchMode(Player player, List<CacheHead> heads) {
		super(player, InventoryType.HEADS, "Search", heads);
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.SEARCH.heads();
	}

	public String getHeadId(CacheHead head) {
		if (!getPlayer().hasPermission("epicheads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
			return "head-no-perms";
		} else {
			return (head.hasCost() && EpicHeads.getInstance().getMainConfig().isEconomyEnabled() ? "head-cost" : "head");
		}
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
		Player player = getPlayer();

		if (!player.hasPermission("epicheads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
			player.sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.search.nopermission", head.getCategory()));
			return;
		}

		if (!EpicHeads.getInstance().chargeForHead(player, head))
			return;

		//Lang.Menu.Search.added(head.getName()).send(player); ToDo: What is this?

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
