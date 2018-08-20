package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.item.Item;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import net.sothatsit.heads.volatilecode.Items;
import org.bukkit.Material;

import java.util.Arrays;

public class Scrollbar extends Element {

    public static final Item defaultLeft = Item.create(Material.ARROW).name("&7Left");
    public static final Item defaultRight = Item.create(Material.ARROW).name("&7Right");
    public static final Item defaultNoLeft = Item.create(Material.AIR);
    public static final Item defaultNoRight = Item.create(Material.AIR);
    public static final Item defaultFiller = Items.createBlackStainedGlassPane().name(" ");

    public static final Template defaultTemplate = new Template(
            defaultLeft, defaultRight,
            defaultNoLeft, defaultNoRight,
            defaultFiller
    );

    private Template template;

    private Button[] items;
    private int index;

    public Scrollbar(Bounds bounds) {
        super(bounds);

        Checks.ensureTrue(bounds.width >= 3, "The width of bounds must be at least 3");
        Checks.ensureTrue(bounds.height == 1, "The height of bounds must be 1");

        this.items = new Button[0];
        this.index = 0;

        setTemplate(defaultTemplate);
    }

    public boolean isScrollActive() {
        return items.length > bounds.width;
    }

    public int getVisibleItems() {
        return isScrollActive() ? bounds.width - 2 : bounds.width;
    }

    public int getMaxScroll() {
        return isScrollActive() ? items.length - bounds.width + 2 : 0;
    }

    public boolean isLeftScrollActive() {
        return isScrollActive() && index > 0;
    }

    public boolean isRightScrollActive() {
        return isScrollActive() && index < getMaxScroll();
    }

    public MenuResponse scrollLeft() {
        if(!isLeftScrollActive())
            return MenuResponse.NONE;

        index--;

        return MenuResponse.UPDATE;
    }

    public MenuResponse scrollRight() {
        if(!isRightScrollActive())
            return MenuResponse.NONE;

        index++;

        return MenuResponse.UPDATE;
    }

    private static int clamp(int num, int min, int max) {
        return (num < min ? min : (num > max ? max : num));
    }

    public void scrollTo(int index) {
        index = clamp(index, 0, items.length - 1);

        int visibleItems = getVisibleItems();

        if(index < this.index) {
            this.index = index;
        } else if(index >= this.index + visibleItems) {
            this.index = index - visibleItems + 1;
        }
    }

    @Override
    public Button[] getItems() {
        Button[] scrollbar = new Button[bounds.getVolume()];

        if(isScrollActive()) {
            if(isLeftScrollActive()) {
                scrollbar[0] = template.constructScrollLeftButton(this);
            } else {
                scrollbar[0] = template.constructNoScrollLeftItem();
            }

            if(isRightScrollActive()) {
                scrollbar[bounds.width - 1] = template.constructScrollRightButton(this);
            } else {
                scrollbar[bounds.width - 1] = template.constructNoScrollRightItem();
            }

            System.arraycopy(items, index, scrollbar, 1, bounds.width - 2);
        } else {
            System.arraycopy(items, 0, scrollbar, 0, items.length);
            Arrays.fill(scrollbar, items.length, bounds.width, template.constructFillerItem());
        }

        return scrollbar;
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
    }

    public void setItems(Button[] items) {
        Checks.ensureNonNull(items, "items");

        this.items = items;
        this.index = 0;
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("items", items)
                .entry("index", index).toString();
    }

    public static final class Template {

        private final Item left;
        private final Item right;
        private final Item noLeft;
        private final Item noRight;
        private final Item filler;

        public Template(Item left, Item right, Item noLeft, Item noRight, Item filler) {
            Checks.ensureNonNull(left, "left");
            Checks.ensureNonNull(right, "right");
            Checks.ensureNonNull(noLeft, "noLeft");
            Checks.ensureNonNull(noRight, "noRight");
            Checks.ensureNonNull(filler, "filler");

            this.left = left;
            this.right = right;
            this.noLeft = noLeft;
            this.noRight = noRight;
            this.filler = filler;
        }

        public Button constructScrollLeftButton(Scrollbar scrollbar) {
            return left.buildButton(scrollbar::scrollLeft);
        }

        public Button constructScrollRightButton(Scrollbar scrollbar) {
            return right.buildButton(scrollbar::scrollRight);
        }

        public Button constructNoScrollLeftItem() {
            return noLeft.buildButton();
        }

        public Button constructNoScrollRightItem() {
            return noRight.buildButton();
        }

        public Button constructFillerItem() {
            return filler.buildButton();
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("left", left)
                    .entry("right", right)
                    .entry("noLeft", noLeft)
                    .entry("noRight", noRight)
                    .entry("filler", filler).toString();
        }

    }

}
