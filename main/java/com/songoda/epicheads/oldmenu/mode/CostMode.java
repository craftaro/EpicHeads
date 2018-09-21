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

		getPlayer().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.categorycost.open", cost));
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.COST.fromType(type);
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
		openInventory(InventoryType.CONFIRM, head, ArrayUtils.create(new Placeholder("%newcost%", cost)));
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		getPlayer().sendMessage(EpicHeads.getInstance().getLocale().getMessage("interface.categorycost.setcost", head.getName(), cost));

		head.setCost(cost);
		EpicHeads.getInstance().saveCache();
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

}
