package com.songoda.epicheads.config.menu;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.menu.CacheHeadsMenu;
import com.songoda.epicheads.util.Methods;

import java.io.File;

public class Menus {

	private MenuConfig browseConfig;
	private CacheHeadsMenu.Template browseTemplate;

	public Menus() {
		browseConfig = new MenuConfig("menus/browse.yml");
	}

	public void reload() {
		File menusFolder = new File(EpicHeads.getInstance().getDataFolder(), "menus");

		if (!menusFolder.exists() && !menusFolder.mkdirs()) {
			Methods.formatText("Unable to create the plugins/Heads/menus folder for Heads menu configuration");
		}

		browseConfig.load();
		browseTemplate = browseConfig.loadCacheHeadsMenu("menu");
		browseConfig.saveIfChanged();
	}

	public CacheHeadsMenu.Template getBrowseTemplate() {
		return browseTemplate;
	}

}
