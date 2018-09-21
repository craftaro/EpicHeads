package com.songoda.epicheads.menu.ui.element;

import com.songoda.epicheads.menu.ui.Bounds;
import com.songoda.epicheads.menu.ui.item.Button;
import com.songoda.epicheads.util.Checks;
import com.songoda.epicheads.util.Stringify;

public abstract class Element {

    public final Bounds bounds;

    public Element(Bounds bounds) {
        Checks.ensureNonNull(bounds, "bounds");

        this.bounds = bounds;
    }

    protected abstract Button[] getItems();

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("bounds", bounds).toString();
    }

}
