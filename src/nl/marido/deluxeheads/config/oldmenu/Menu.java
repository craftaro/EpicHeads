package nl.marido.deluxeheads.config.oldmenu;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.config.lang.Placeholder;
import nl.marido.deluxeheads.menu.ui.item.Item;

@Deprecated
public class Menu {

	private Function<String, Boolean> FILTER_ECONOMY_LINES_OUT = line -> !line.contains("%cost%");

	private String title;
	private final Map<String, Item> items = new HashMap<>();
	private final Menu defaults;

	public Menu() {
		this(null);
	}

	public Menu(Menu defaults) {
		this.defaults = defaults;
	}

	public String getTitle(Placeholder... placeholders) {
		return title != null ? Placeholder.applyAll(title, placeholders) : "Menu";
	}

	public Item getItem(String name) {
		Item item = items.get(name.toLowerCase());

		return item != null ? item : getDefaultItem(name);
	}

	public ItemStack getItemStack(String name, Placeholder... placeholders) {
		Item item = getItem(name);

		return item != null ? item.build(getItemLoreFilter(), placeholders) : null;
	}

	private Item getDefaultItem(String name) {
		return defaults != null ? defaults.getItem(name) : null;
	}

	private Function<String, Boolean> getItemLoreFilter() {
		return DeluxeHeads.getMainConfig().isEconomyEnabled() ? null : FILTER_ECONOMY_LINES_OUT;
	}

	public void load(String filename, ConfigurationSection section, AtomicBoolean shouldSave) {
		for (String key : section.getKeys(false)) {
			if (!section.isConfigurationSection(key)) {
				loadValue(section, key);
				continue;
			}

			Item item = Item.load(filename, section.getConfigurationSection(key), shouldSave);

			if (item == null)
				continue;

			items.put(key.toLowerCase(), item);
		}
	}

	private void loadValue(ConfigurationSection section, String key) {
		if (key.equals("title")) {
			title = section.getString(key, null);
			return;
		}

		DeluxeHeads.warning("Unknown use of value \"" + key + "\" in menu \"" + section.getCurrentPath() + "\"");
	}

	public static Menu loadMenu(String filename, ConfigurationSection section, AtomicBoolean shouldSave) {
		return loadMenu(filename, section, shouldSave, null);
	}

	public static Menu loadMenu(String filename, ConfigurationSection section, AtomicBoolean shouldSave, Menu defaults) {
		Menu menu = new Menu(defaults);

		menu.load(filename, section, shouldSave);

		return menu;
	}

}
