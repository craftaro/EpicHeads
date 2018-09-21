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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class CategoryCostMode extends BaseMode {

	private Double cost = null;

	public CategoryCostMode(Player player) {
		super(player);
	}

	public void setCost(Double cost) {
		this.cost = cost;

		EpicHeads.getInstance().getLocale().getMessage("interface.categorycost.open", cost);
	}

	@Override
	public Menu getMenu(InventoryType type) {
		return Menus.CATEGORY_COST.fromType(type);
	}

	public CacheHead getCategoryHead(String category) {
		List<CacheHead> heads = EpicHeads.getCache().getCategoryHeads(category);

		return (heads.size() > 0 ? heads.get(0) : null);
	}

	@Override
	public void onCategorySelect(String category) {
		CacheHead head = getCategoryHead(category);

		if (head == null) {
			getPlayer().sendMessage(ChatColor.RED + "Invalid category");
			return;
		}

		openInventory(InventoryType.CONFIRM, head, ArrayUtils.create(new Placeholder("%newcost%", cost)));
	}

	@Override
	public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
		EpicHeads.getInstance().getLocale().getMessage("interface.categorycost.setcost", head.getCategory(), cost);

		EpicHeads.getMainConfig().setCategoryCost(head.getCategory(), cost);
	}

	@Override
	public boolean canOpenCategory(String category) {
		return true;
	}

	@Override
	public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
	}

}
