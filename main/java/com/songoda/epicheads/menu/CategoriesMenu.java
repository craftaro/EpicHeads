package com.songoda.epicheads.menu;

import com.songoda.epicheads.cache.CacheFile;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.config.lang.Placeholder;
import com.songoda.epicheads.menu.ui.Bounds;
import com.songoda.epicheads.menu.ui.MenuResponse;
import com.songoda.epicheads.menu.ui.element.Element;
import com.songoda.epicheads.menu.ui.element.PagedBox;
import com.songoda.epicheads.menu.ui.item.Button;
import com.songoda.epicheads.menu.ui.item.Item;
import com.songoda.epicheads.util.Checks;
import com.songoda.epicheads.util.SafeCall;
import com.songoda.epicheads.util.Stringify;
import com.songoda.epicheads.volatilecode.Items;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class CategoriesMenu extends Element {

    public static final Item defaultCategoryItem = Items.createSkull()
            .name("&7%category%")
            .lore("&6%heads% &eheads");

    public static final Template defaultTemplate = new Template(PagedBox.defaultTemplate, defaultCategoryItem);

    private Template template;

    private final CacheFile cache;
    private final Function<String, MenuResponse> onSelect;

    private final PagedBox pagedBox;

    public CategoriesMenu(CacheFile cache, Bounds bounds, Function<String, MenuResponse> onSelect) {
        super(bounds);

        Checks.ensureNonNull(cache, "cache");
        Checks.ensureNonNull(onSelect, "onSelect");
        Checks.ensureTrue(bounds.height >= 3, "bounds must have a height of at least 3");

        this.cache = cache;
        this.onSelect = SafeCall.nonNullFunction(onSelect, "onSelect");
        this.pagedBox = new PagedBox(bounds);

        setTemplate(defaultTemplate, PagedBox.defaultLeftControl, PagedBox.defaultRightControl);

        updateItems();
    }

    @Override
    public Button[] getItems() {
        return pagedBox.getItems();
    }

    private void updateItems() {
        List<String> categories = new ArrayList<>(cache.getCategories());
        Button[] categoryItems = new Button[categories.size() * 2 + 4];

        Collections.sort(categories);

        for(int index = 0; index < categories.size(); ++index) {
            String category = categories.get(index);

            categoryItems[index * 2] = template.constructCategoryButton(this, category);
        }

        pagedBox.setItems(categoryItems);
    }

    public void setTemplate(Template template, Button leftControl, Button rightControl) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this, leftControl, rightControl);
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("onSelect", onSelect)
                .entry("pagedBox", pagedBox).toString();
    }

    public static final class Template {

        private final PagedBox.Template pagedBoxTemplate;
        private final Item categoryItem;

        public Template(PagedBox.Template pagedBoxTemplate, Item categoryItem) {
            Checks.ensureNonNull(pagedBoxTemplate, "pagedBoxTemplate");
            Checks.ensureNonNull(categoryItem, "categoryItem");

            this.pagedBoxTemplate = pagedBoxTemplate;
            this.categoryItem = categoryItem;
        }

        private void init(CategoriesMenu menu, Button leftControl, Button rightControl) {
            menu.pagedBox.setTemplate(pagedBoxTemplate, leftControl, rightControl);
        }

        public Button constructCategoryButton(CategoriesMenu menu, String category) {
            Checks.ensureNonNull(menu, "menu");
            Checks.ensureNonNull(category, "category");

            List<CacheHead> categoryHeads = menu.cache.getCategoryHeads(category);
            CacheHead iconHead = categoryHeads.get(0);

            Placeholder categoryPlaceholder = new Placeholder("%category%", category);
            Placeholder headCountPlaceholder = new Placeholder("%heads%", categoryHeads.size());

            ItemStack icon = categoryItem.build(categoryPlaceholder, headCountPlaceholder);
            icon = iconHead.addTexture(icon);

            return new Button(icon, () -> menu.onSelect.apply(category));
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("pagedBoxTemplate", pagedBoxTemplate)
                    .entry("categoryItem", categoryItem).toString();
        }

    }

}
