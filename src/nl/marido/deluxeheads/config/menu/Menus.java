package nl.marido.deluxeheads.config.menu;

import java.io.File;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.menu.CacheHeadsMenu;

public class Menus {

	private MenuConfig browseConfig;
	private CacheHeadsMenu.Template browseTemplate;

	public Menus() {
		browseConfig = new MenuConfig("menus/browse.yml");
	}

	public void reload() {
		File menusFolder = new File(DeluxeHeads.getInstance().getDataFolder(), "menus");

		if (!menusFolder.exists() && !menusFolder.mkdirs()) {
			DeluxeHeads.print("Unable to create the plugins/Heads/menus folder for Heads menu configuration");
		}

		browseConfig.load();
		browseTemplate = browseConfig.loadCacheHeadsMenu("menu");
		browseConfig.saveIfChanged();
	}

	public CacheHeadsMenu.Template getBrowseTemplate() {
		return browseTemplate;
	}

}
