package net.sothatsit.heads.menu;

import net.sothatsit.heads.menu.ui.element.Element;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheFile;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.menu.ui.*;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.menu.ui.item.Item;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;

public class CacheHeadsMenu extends Element {

    public static final Item defaultClose = Item.create(Material.REDSTONE_BLOCK).name("&cClose Menu");
    public static final Item defaultBack = Item.create(Material.REDSTONE_BLOCK).name("&cBack to Categories");
    public static final Item defaultSearch = Item.create(Material.COMPASS).name("&7Search Heads");
    public static final String defaultCategoriesTitle = "Categories";
    public static final String defaultCategoryTitle = "%category%";

    public static final Template defaultTemplate = new Template(
            CategoriesMenu.defaultTemplate, HeadsMenu.defaultTemplate,
            defaultClose, defaultBack, defaultSearch,
            defaultCategoriesTitle, defaultCategoryTitle
    );

    private Template template;

    private final CacheFile cache;
    private final InventoryMenu inventoryMenu;

    private final CategoriesMenu categoriesMenu;
    private final HeadsMenu headsMenu;

    private String selectedCategory = null;

    public CacheHeadsMenu(CacheFile cache, InventoryMenu inventoryMenu, Bounds bounds,
                          Function<CacheHead, MenuResponse> onSelect) {
        super(bounds);

        Checks.ensureNonNull(cache, "cache");
        Checks.ensureNonNull(inventoryMenu, "inventoryMenu");
        Checks.ensureNonNull(onSelect, "onSelect");
        Checks.ensureTrue(bounds.height >= 3, "bounds must have a height of at least 3");

        this.cache = cache;
        this.inventoryMenu = inventoryMenu;

        this.categoriesMenu = new CategoriesMenu(cache, bounds, this::selectCategory);
        this.headsMenu = new HeadsMenu(bounds, onSelect);

        setTemplate(defaultTemplate);
    }

    public boolean onCategoriesScreen() {
        return selectedCategory == null;
    }

    public MenuResponse close() {
        return MenuResponse.CLOSE;
    }

    public MenuResponse back() {
        this.selectedCategory = null;

        inventoryMenu.setTitle(template.getCategoriesTitle());

        return MenuResponse.UPDATE;
    }

    public MenuResponse search() {
        inventoryMenu.getPlayer().sendMessage("Search");

        return MenuResponse.NONE;
    }

    public MenuResponse selectCategory(String category) {
        Checks.ensureNonNull(category, "category");

        List<CacheHead> heads = cache.getCategoryHeads(category);

        if(heads.size() == 0) {
            return back();
        }

        this.selectedCategory = category;
        this.headsMenu.setItems(heads);

        inventoryMenu.setTitle(template.getCategoryTitle(category));

        return MenuResponse.UPDATE;
    }

    @Override
    public Button[] getItems() {
        if(onCategoriesScreen()) {
            return categoriesMenu.getItems();
        } else {
            return headsMenu.getItems();
        }
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this);

        if(onCategoriesScreen()) {
            inventoryMenu.setTitle(template.getCategoriesTitle());
        } else {
            inventoryMenu.setTitle(template.getCategoryTitle(selectedCategory));
        }
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("cache", cache)
                .entry("headsMenu", headsMenu)
                .entry("categoriesMenu", categoriesMenu).toString();
    }

    public static final class Template {

        private final CategoriesMenu.Template categoriesTemplate;
        private final HeadsMenu.Template headsTemplate;
        private final Item close;
        private final Item back;
        private final Item search;
        private final String categoriesTitle;
        private final String categoryTitle;

        public Template(CategoriesMenu.Template categoriesTemplate, HeadsMenu.Template headsTemplate,
                        Item close, Item back, Item search, String categoriesTitle, String categoryTitle) {

            Checks.ensureNonNull(categoriesTemplate, "categoriesTemplate");
            Checks.ensureNonNull(headsTemplate, "headsTemplate");
            Checks.ensureNonNull(close, "close");
            Checks.ensureNonNull(back, "back");
            Checks.ensureNonNull(search, "search");
            Checks.ensureNonNull(categoriesTemplate, "categoriesTemplate");
            Checks.ensureNonNull(categoryTitle, "categoryTitle");

            this.categoriesTemplate = categoriesTemplate;
            this.headsTemplate = headsTemplate;
            this.close = close;
            this.back = back;
            this.search = search;
            this.categoriesTitle = ChatColor.translateAlternateColorCodes('&', categoriesTitle);
            this.categoryTitle = ChatColor.translateAlternateColorCodes('&', categoryTitle);
        }

        public String getCategoriesTitle() {
            return categoriesTitle;
        }

        public String getCategoryTitle(String category) {
            return categoryTitle.replace("%category%", category);
        }

        private void init(CacheHeadsMenu menu) {
            Button close = this.close.buildButton(menu::close);
            Button back = this.back.buildButton(menu::back);
            Button search = this.search.buildButton(menu::search);

            menu.categoriesMenu.setTemplate(categoriesTemplate, close, search);
            menu.headsMenu.setTemplate(headsTemplate, back, search);
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("categoriesTemplate", categoriesTemplate)
                    .entry("headsTemplate", headsTemplate).toString();
        }

    }

    public static void openHeadsMenu(Player player) {
        InventoryMenu inventory = new InventoryMenu(player, "Heads", 6);

        CacheHeadsMenu menu = new CacheHeadsMenu(Heads.getCache(), inventory, inventory.bounds, head -> {
            player.sendMessage(head.getName());
            return MenuResponse.NONE;
        });

        menu.setTemplate(Heads.getMenus().getBrowseTemplate());

        inventory.addElement(menu);
        inventory.open();
    }

}
