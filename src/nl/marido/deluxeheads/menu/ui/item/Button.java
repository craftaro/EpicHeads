package nl.marido.deluxeheads.menu.ui.item;

import org.bukkit.inventory.ItemStack;

import nl.marido.deluxeheads.menu.ui.MenuResponse;
import nl.marido.deluxeheads.util.Checks;
import nl.marido.deluxeheads.util.SafeCall;
import nl.marido.deluxeheads.util.Stringify;
import nl.marido.deluxeheads.util.SafeCall.SafeCallable;

import java.util.concurrent.Callable;

public class Button {

    private ItemStack item;
    private final SafeCallable<MenuResponse> onClick;

    public Button(ItemStack item) {
        this(item, () -> MenuResponse.NONE);
    }

    public Button(ItemStack item, Callable<MenuResponse> onClick) {
        Checks.ensureNonNull(item, "item");
        Checks.ensureNonNull(onClick, "onClick");

        this.item = item;
        this.onClick = SafeCall.nonNullCallable(onClick, "onClick");
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        Checks.ensureNonNull(item, "item");

        this.item = item;
    }

    public MenuResponse handleClick() {
        return onClick.call();
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("item", item)
                .entry("onClick", onClick).toString();
    }

}
