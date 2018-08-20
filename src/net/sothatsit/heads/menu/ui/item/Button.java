package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.util.SafeCall.SafeCallable;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.inventory.ItemStack;

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
