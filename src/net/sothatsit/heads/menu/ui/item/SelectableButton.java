package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class SelectableButton extends Button {

    private final ButtonGroup group;
    private final ItemStack unselectedItem;
    private final ItemStack selectedItem;
    private boolean selected;

    public SelectableButton(ButtonGroup group,
                            ItemStack unselectedItem,
                            ItemStack selectedItem,
                            Callable<MenuResponse> onClick) {
        super(unselectedItem, onClick);

        Checks.ensureNonNull(group, "group");
        Checks.ensureNonNull(unselectedItem, "unselectedItem");
        Checks.ensureNonNull(selectedItem, "selectedItem");

        this.group = group;
        this.unselectedItem = unselectedItem;
        this.selectedItem = selectedItem;
        this.selected = false;

        if(group != null) {
            group.addButton(this);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if(this.selected == selected)
            return;

        if(selected && group != null) {
            group.unselectAll();
        }

        this.selected = selected;
        this.setItem(selected ? selectedItem : unselectedItem);
    }

    @Override
    public MenuResponse handleClick() {
        setSelected(true);

        return super.handleClick();
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .previous(super.toString())
                .entry("selectedItem", selectedItem)
                .entry("selected", selected).toString();
    }

}
