package net.sothatsit.heads.config.menu;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.ConfigFile;
import net.sothatsit.heads.config.FileConfigFile;
import net.sothatsit.heads.menu.CacheHeadsMenu;
import net.sothatsit.heads.menu.CategoriesMenu;
import net.sothatsit.heads.menu.HeadsMenu;
import net.sothatsit.heads.menu.ui.item.Item;
import net.sothatsit.heads.menu.ui.element.Scrollbar;
import net.sothatsit.heads.menu.ui.element.PagedBox;
import net.sothatsit.heads.util.Checks;

import java.util.concurrent.atomic.AtomicBoolean;

public class MenuConfig {

    private final ConfigFile config;
    private final AtomicBoolean requiresSave;

    public MenuConfig(String fileName) {
        this(Heads.getVersionedConfig(fileName));
    }

    public MenuConfig(ConfigFile config) {
        Checks.ensureNonNull(config, "configFile");

        this.config = config;
        this.requiresSave = new AtomicBoolean(false);
    }

    public void load() {
        config.copyDefaults();
        config.reload();

        requiresSave.set(false);
    }

    public void saveIfChanged() {
        if(!requiresSave.get())
            return;

        config.save();
    }

    public Scrollbar.Template loadScrollbar(String key) {
        Item left    = config.getOrCopyDefault(key + ".left",     Scrollbar.defaultLeft,    requiresSave);
        Item right   = config.getOrCopyDefault(key + ".right",    Scrollbar.defaultRight,   requiresSave);
        Item noLeft  = config.getOrCopyDefault(key + ".no-left",  Scrollbar.defaultNoLeft,  requiresSave);
        Item noRight = config.getOrCopyDefault(key + ".no-right", Scrollbar.defaultNoRight, requiresSave);
        Item filler  = config.getOrCopyDefault(key + ".filler",   Scrollbar.defaultFiller,  requiresSave);

        return new Scrollbar.Template(left, right, noLeft, noRight, filler);
    }

    public PagedBox.Template loadPagedBox(String key) {
        Item unselected = config.getOrCopyDefault(key + ".unselected-page", PagedBox.defaultUnselected, requiresSave);
        Item selected   = config.getOrCopyDefault(key + ".selected-page",   PagedBox.defaultSelected,   requiresSave);

        Scrollbar.Template scrollbar = loadScrollbar(key + ".scrollbar");

        return new PagedBox.Template(scrollbar, unselected, selected);
    }

    public CategoriesMenu.Template loadCategoriesMenu(String key) {
        Item category = config.getOrCopyDefault(key + ".category", CategoriesMenu.defaultCategoryItem, requiresSave);

        PagedBox.Template pagedBoxTemplate = loadPagedBox(key);

        return new CategoriesMenu.Template(pagedBoxTemplate, category);
    }

    public HeadsMenu.Template loadHeadsMenu(String key) {
        Item head = config.getOrCopyDefault(key + ".head", HeadsMenu.defaultHead, requiresSave);

        PagedBox.Template pagedBoxTemplate = loadPagedBox(key);

        return new HeadsMenu.Template(pagedBoxTemplate, head);
    }

    public CacheHeadsMenu.Template loadCacheHeadsMenu(String key) {
        String categoriesTitle = config.getOrCopyDefault(key + ".categories-title", CacheHeadsMenu.defaultCategoriesTitle, requiresSave);
        String categoryTitle = config.getOrCopyDefault(key + ".category-title", CacheHeadsMenu.defaultCategoryTitle, requiresSave);

        Item close = config.getOrCopyDefault(key + ".close", CacheHeadsMenu.defaultClose, requiresSave);
        Item back = config.getOrCopyDefault(key + ".back", CacheHeadsMenu.defaultBack, requiresSave);
        Item search = config.getOrCopyDefault(key + ".search", CacheHeadsMenu.defaultSearch, requiresSave);

        CategoriesMenu.Template categoriesTemplate = loadCategoriesMenu(key + ".categories");
        HeadsMenu.Template headsTemplate = loadHeadsMenu(key + ".heads");

        return new CacheHeadsMenu.Template(categoriesTemplate, headsTemplate, close, back, search, categoriesTitle, categoryTitle);
    }

}
