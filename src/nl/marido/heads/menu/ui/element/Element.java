package nl.marido.heads.menu.ui.element;

import nl.marido.heads.menu.ui.Bounds;
import nl.marido.heads.menu.ui.item.Button;
import nl.marido.heads.util.Checks;
import nl.marido.heads.util.Stringify;

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
