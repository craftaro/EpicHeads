package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.Position;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.util.Checks;

import java.util.Arrays;

public final class Container extends Element {

    private final Button[] items;

    public Container(Bounds bounds) {
        super(bounds);

        this.items = new Button[bounds.getVolume()];
    }

    @Override
    public Button[] getItems() {
        return items;
    }

    public void addElement(Element element) {
        setItems(element.bounds, element.getItems());
    }

    public void setItems(Bounds bounds, Button[] items) {
        Checks.ensureNonNull(bounds, "bounds");
        Checks.ensureNonNull(items, "items");
        Checks.ensureTrue(items.length == bounds.getVolume(), "length of items does not match the volume of bounds");
        Checks.ensureTrue(this.bounds.inBounds(bounds), "bounds is not within the bounds of the container");

        for(int x = 0; x < bounds.width; x++) {
            for(int y = 0; y < bounds.height; y++) {
                Position fromPos = new Position(x, y);
                Position toPos = fromPos.add(bounds.position);

                this.items[toPos.toSerialIndex(this.bounds.width)] = items[fromPos.toSerialIndex(bounds.width)];
            }
        }
    }

    public void setItem(int x, int y, Button item) {
        setItem(new Position(x, y), item);
    }

    public void setItem(Position position, Button item) {
        Checks.ensureNonNull(position, "position");
        Checks.ensureTrue(bounds.inBounds(position), "position is not within the bounds of the container");

        items[position.toSerialIndex(bounds.width)] = item;
    }

    public void clear() {
        Arrays.fill(items, null);
    }

    public MenuResponse handleClick(int slot) {
        Checks.ensureTrue(slot >= 0, "slot cannot be less than 0");
        Checks.ensureTrue(slot < items.length, "slot must be less than the volume of the container");

        Button item = items[slot];

        return item == null ? MenuResponse.NONE : item.handleClick();
    }

}
