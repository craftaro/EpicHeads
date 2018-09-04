package nl.marido.deluxeheads.menu.ui.element;

import nl.marido.deluxeheads.menu.ui.Bounds;
import nl.marido.deluxeheads.menu.ui.item.Button;
import nl.marido.deluxeheads.util.Checks;
import nl.marido.deluxeheads.util.Stringify;

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
