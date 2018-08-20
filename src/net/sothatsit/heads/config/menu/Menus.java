package net.sothatsit.heads.config.menu;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.menu.CacheHeadsMenu;

import java.io.File;

public class Menus {

    private MenuConfig browseConfig;
    private CacheHeadsMenu.Template browseTemplate;

    public Menus() {
        browseConfig = new MenuConfig("menus/browse.yml");
    }

    public void reload() {
        File menusFolder = new File(Heads.getInstance().getDataFolder(), "menus");

        if(!menusFolder.exists() && !menusFolder.mkdirs()) {
            Heads.severe("Unable to create the plugins/Heads/menus folder for Heads menu configuration");
        }

        browseConfig.load();
        browseTemplate = browseConfig.loadCacheHeadsMenu("menu");
        browseConfig.saveIfChanged();
    }

    public CacheHeadsMenu.Template getBrowseTemplate() {
        return browseTemplate;
    }

}
