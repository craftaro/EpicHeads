package net.sothatsit.heads.oldmenu;

import java.util.*;

import net.sothatsit.heads.cache.CacheFile;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.volatilecode.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.oldmenu.mode.InvMode;
import org.bukkit.inventory.meta.ItemMeta;

public class CategorySelectMenu extends AbstractModedInventory {
    
    private Map<String, List<CacheHead>> heads;
    private List<String> categories;
    private double offset;
    
    public CategorySelectMenu(InvMode mode) {
        super(InventoryType.CATEGORY, mode);

        recreate();
    }
    
    @Override
    public void recreate() {
        CacheFile cache = Heads.getCache();

        this.heads = new HashMap<>();
        this.categories = new ArrayList<>();

        if(Heads.getMainConfig().shouldHideNoPermCategories()) {
            Player player = this.getInvMode().getPlayer();

            for(String category : cache.getCategories()) {
                if(player.hasPermission("heads.category." + category.toLowerCase().replace(' ', '_'))) {
                    this.categories.add(category);
                }
            }
        } else {
            this.categories.addAll(cache.getCategories());
        }

        int numHeads = this.categories.size();

        ItemStack[] contents;

        if(numHeads == 0) {
            int size = 6 * 9;

            setInventory(Bukkit.createInventory(this, size, getMenu().getTitle()));

            contents = new ItemStack[size];

            ItemStack red = Items.createRedStainedGlassPane().build();
            ItemMeta meta = red.getItemMeta();

            String message = "&cYou do not have permission";
            String lore =    "&cto view any head categories";

            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', message));
            meta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', lore)));

            red.setItemMeta(meta);

            ItemStack black = red.clone();

            black.setDurability((short) 15);

            Arrays.fill(contents, red);

            contents[1] = black;
            contents[7] = black;
            contents[1 + 9 * 5] = black;
            contents[7 + 9 * 5] = black;

            contents[4 + 9] = black;
            contents[4 + 9 * 2] = black;
            contents[4 + 9 * 4] = black;

            for(int y = 0; y < 6; y++) {
                contents[y * 9] = black;
                contents[8 + y * 9] = black;
            }
        } else if(numHeads > 27) {
        	int size = (int) Math.ceil(numHeads / 9d) * 9;
            
            setInventory(Bukkit.createInventory(this, size, getMenu().getTitle()));
            
            int lastRow = numHeads % 5;
            
            this.offset = (9d - lastRow) / 2d;
            
            contents = new ItemStack[size];

            for (int index = 0; index < this.categories.size(); index++) {
                String category = this.categories.get(index);
                List<CacheHead> heads = new ArrayList<>(cache.getCategoryHeads(category));

                this.heads.put(category, heads);
                
                int slot = index;
                
                if (slot >= size - 9) {
                    slot += (int) Math.floor(this.offset);
                    
                    if (slot % 9 >= 4) {
                        slot += (int) Math.ceil(this.offset % 1);
                    }
                }
                
                CacheHead head = heads.get(0);

                ItemStack item = getMenu().getItemStack("head",
                        new Placeholder("%category%", category),
                        new Placeholder("%heads%", Integer.toString(heads.size())));

                contents[slot] = head.addTexture(item);
            }
        } else {
        	int rows = (int) Math.ceil(numHeads / 9d);
        	
        	if(numHeads <= rows * 9 - 4) {
        		rows = rows * 2 - 1;
        	} else {
        		rows = rows * 2;
        	}
        	
        	int size = rows * 9;
        	
        	setInventory(Bukkit.createInventory(this, size, getMenu().getTitle()));
        	
        	contents = new ItemStack[size];
        	
        	for (int index = 0; index < this.categories.size(); index++) {
                String category = this.categories.get(index);
                List<CacheHead> heads = new ArrayList<>(cache.getCategoryHeads(category));

                this.heads.put(category, heads);
                
                CacheHead head = heads.get(0);

                ItemStack item = getMenu().getItemStack("head",
                        new Placeholder("%category%", category),
                        new Placeholder("%heads%", Integer.toString(heads.size())));

                contents[index * 2] = head.addTexture(item);
            }
        }
        
        getInventory().setContents(contents);
    }
    
    public String getCategory(int slot) {
        Inventory inv = getInventory();
        int size = inv.getSize();

        if (slot < 0 || slot >= size || inv.getItem(slot) == null)
            return null;

        if(this.categories.size() > 27) {
            int index;

            if (slot >= size - 9) {
                if (slot % 9 >= 4) {
                    index = slot - (int) Math.ceil(this.offset);
                } else {
                    index = slot - (int) Math.floor(this.offset);
                }
            } else {
                index = slot;
            }
            
            return this.categories.get(index);
        } else {
        	return this.categories.get(slot / 2);
        }
    }
    
    public List<CacheHead> getHeads(String category) {
        return heads.get(category);
    }
    
}
