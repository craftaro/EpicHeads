package net.sothatsit.heads.menu.ui;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.menu.ui.element.Container;
import net.sothatsit.heads.menu.ui.element.Element;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryMenu implements InventoryHolder {

    private final Player player;
    public final Bounds bounds;

    private final List<Element> elements = new ArrayList<>();

    private Container container;
    private Inventory inventory;
    private Inventory newInventory;

    public InventoryMenu(Player player, String title, int rows) {
        Checks.ensureNonNull(player, "player");

        this.player = player;
        this.bounds = new Bounds(Position.ZERO, 9, rows);
        this.container = new Container(bounds);

        setTitle(title);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public boolean hasMenuOpen() {
        InventoryView view = player.getOpenInventory();

        if (view == null || view.getTopInventory() == null)
            return false;

        InventoryHolder holder = view.getTopInventory().getHolder();

        return holder != null && holder.equals(this);
    }

    public void removeElement(Element element) {
        Checks.ensureNonNull(element, "element");

        elements.remove(element);
    }

    public void addElement(Element element) {
        Checks.ensureNonNull(element, "element");
        Checks.ensureTrue(bounds.inBounds(element.bounds), "element's bounds is not within the bounds of the menu");

        elements.add(element);
    }

    public List<Element> getElements() {
        return elements;
    }

    public void open() {
        updateMenu();
        player.openInventory(inventory);
    }

    public void setTitle(String title) {
        Checks.ensureNonNull(title, "title");

        if(inventory != null && title.equals(inventory.getTitle()))
            return;

        title = (title.length() > 32 ? title.substring(0, 32) : title);

        this.newInventory = Bukkit.createInventory(this, bounds.getVolume(), title);
    }

    private boolean swapToNewInventory() {
        if(newInventory == null)
            return false;

        inventory = newInventory;
        newInventory = null;

        return true;
    }

    public void layoutElements() {
        container.clear();

        elements.forEach(container::addElement);
    }

    public void updateMenu() {
        boolean newInventory = swapToNewInventory();

        layoutElements();

        Button[] items = container.getItems();
        ItemStack[] contents = new ItemStack[items.length];

        for(int index = 0; index < contents.length; index++) {
            Button item = items[index];

            if(item != null) {
                contents[index] = item.getItem();
            }
        }

        inventory.setContents(contents);

        if(newInventory && hasMenuOpen()) {
            player.openInventory(inventory);
        }
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // Make sure the player's inventory is up to date after the event is cancelled
        Bukkit.getScheduler().scheduleSyncDelayedTask(Heads.getInstance(), player::updateInventory, 1);

        int slot = event.getRawSlot();

        MenuResponse response = container.handleClick(slot);

        switch (response) {
            case CLOSE:
                player.closeInventory();
                break;
            case UPDATE:
                updateMenu();
                break;
            case NONE:
                break;
            default:
                throw new IllegalStateException("Unknown MenuResponse value " + response);
        }
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .previous(super.toString())
                .entry("inventory", inventory)
                .entry("player", player).toString();
    }

}