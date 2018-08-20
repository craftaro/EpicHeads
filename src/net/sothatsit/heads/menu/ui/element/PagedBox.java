package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.item.*;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import net.sothatsit.heads.volatilecode.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PagedBox extends Element {

    public static final Item defaultUnselected = Item.create(Material.PAPER).name("&7Page %page%");
    public static final Item defaultSelected = Items.createEmptyMap().name("&7Page %page%");
    public static final Button defaultLeftControl = Item.create(Material.REDSTONE_BLOCK).name("&cNo left control").buildButton();
    public static final Button defaultRightControl = Item.create(Material.REDSTONE_BLOCK).name("&cNo right control").buildButton();

    public static final Template defaultTemplate = new Template(
            Scrollbar.defaultTemplate, defaultUnselected, defaultSelected
    );

    private Template template;

    private final Scrollbar scrollbar;
    private ButtonGroup pageButtons;

    private Button leftControl;
    private Button rightControl;

    private Button[] items;
    private int page;

    public PagedBox(Bounds bounds) {
        super(bounds);

        Checks.ensureTrue(bounds.height >= 2, "bounds height must be at least 2");
        Checks.ensureTrue(bounds.width >= 5, "bounds width must be at least 3");

        Bounds scrollbarBounds = new Bounds(1, bounds.height - 1, bounds.width - 2, 1);

        this.scrollbar = new Scrollbar(scrollbarBounds);
        this.pageButtons = new ButtonGroup();

        this.items = new Button[0];
        this.page = 0;

        setTemplate(defaultTemplate, defaultLeftControl, defaultRightControl);
    }

    public boolean isScrollbarActive() {
        return items.length > bounds.getVolume();
    }

    private Bounds getPageBounds() {
        return isScrollbarActive() ? new Bounds(bounds.position, bounds.width, bounds.height - 1) : bounds;
    }

    public int getPageSize() {
        return getPageBounds().getVolume();
    }

    public int getPages() {
        int pageSize = getPageSize();

        return (items.length + pageSize - 1) / pageSize;
    }

    private static int clamp(int num, int min, int max) {
        return (num < min ? min : (num > max ? max : num));
    }

    public void setPage(int page) {
        this.page = clamp(page, 0, getPages() - 1);

        scrollbar.scrollTo(page);
        pageButtons.select(page);
    }

    @Override
    public Button[] getItems() {
        Container container = new Container(bounds);

        container.setItems(getPageBounds(), getPageContents());

        container.addElement(scrollbar);
        container.setItem(0, bounds.height - 1, leftControl);
        container.setItem(bounds.width - 1, bounds.height - 1, rightControl);

        return container.getItems();
    }

    private Button[] getPageContents() {
        int pageSize = getPageSize();

        int from = page * pageSize;
        int to = Math.min((page + 1) * pageSize, items.length);

        if(to <= from)
            return new Button[pageSize];

        Button[] pageContents = new Button[pageSize];

        System.arraycopy(items, from, pageContents, 0, to - from);

        return pageContents;
    }

    public void setTemplate(Template template, Button leftControl, Button rightControl) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.leftControl = leftControl;
        this.rightControl = rightControl;
        this.template.init(this);

        setupPageScrollbar();
    }

    public void setItems(Button[] items) {
        Checks.ensureNonNull(items, "items");

        this.items = items;
        this.page = 0;

        setupPageScrollbar();
    }

    private void setupPageScrollbar() {
        int pages = getPages();

        Button[] pageItems = new Button[pages];

        pageButtons = new ButtonGroup();

        for(int page = 0; page < pages; page++) {
            SelectableButton pageButton = template.constructPageButton(this, pageButtons, page);

            pageButton.setSelected(page == this.page);

            pageItems[page] = pageButton;
        }

        scrollbar.setItems(pageItems);
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("scrollbar", scrollbar)
                .entry("pageButtons", pageButtons)
                .entry("leftControl", leftControl)
                .entry("rightControl", rightControl)
                .entry("page", page).toString();
    }

    public static final class Template {

        private final Scrollbar.Template scrollbar;
        private final Item unselected;
        private final Item selected;

        public Template(Scrollbar.Template scrollbar, Item unselected, Item selected) {

            Checks.ensureNonNull(scrollbar, "scrollbar");
            Checks.ensureNonNull(unselected, "unselected");
            Checks.ensureNonNull(selected, "selected");

            this.scrollbar = scrollbar;
            this.unselected = unselected;
            this.selected = selected;
        }

        public void init(PagedBox pagedBox) {
            pagedBox.scrollbar.setTemplate(scrollbar);
        }

        private ItemStack constructPageItem(Item templateItem, int page) {
            int humanPage = page + 1;
            Placeholder pagePlaceholder = new Placeholder("%page%", humanPage);

            ItemStack item = templateItem.build(pagePlaceholder);
            item.setAmount(humanPage > 60 ? (humanPage % 10 == 0 ? 10 : humanPage % 10) : humanPage);

            return item;
        }

        public ItemStack constructUnselectedPageItem(int page) {
            return constructPageItem(unselected, page);
        }

        public ItemStack constructSelectedPageItem(int page) {
            return constructPageItem(selected, page);
        }

        public SelectableButton constructPageButton(PagedBox pagedBox, ButtonGroup group, int page) {
            ItemStack unselectedItem = constructUnselectedPageItem(page);
            ItemStack selectedItem = constructSelectedPageItem(page);

            return new SelectableButton(group, unselectedItem, selectedItem, () -> {
                pagedBox.setPage(page);
                return MenuResponse.UPDATE;
            });
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("scrollbar", scrollbar)
                    .entry("unselected", unselected)
                    .entry("selected", selected).toString();
        }

    }

}
