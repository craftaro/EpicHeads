package net.sothatsit.heads.config.oldmenu;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.oldmenu.InventoryType;

public class Menus {
    
    public static final String SPLIT = "-";
    
    public static final String CATEGORIES = "categories";
    public static final String HEADS = "heads";
    public static final String CONFIRM = "confirm";
    
    public static final MenusGroup GET = new MenusGroup("get");
    public static final MenusGroup SEARCH = new MenusGroup("search");
    public static final MenusGroup REMOVE = new MenusGroup("remove");
    public static final MenusGroup RENAME = new MenusGroup("rename");
    public static final MenusGroup COST = new MenusGroup("cost");
    public static final MenusGroup CATEGORY_COST = new MenusGroup("category-cost");
    public static final MenusGroup CATEGORY_COST_REMOVE = new MenusGroup("category-cost-remove");
    public static final MenusGroup ID = new MenusGroup("id");
    
    public static Menu get(String name) {
        return Heads.getMenuConfig().getMenu(name);
    }
    
    public static class MenusGroup {
        private String prefix;
        
        public MenusGroup(String prefix) {
            this.prefix = prefix;
        }
        
        public String getPrefix() {
            return prefix;
        }
        
        public String getCategoriesName() {
            return prefix + SPLIT + CATEGORIES;
        }
        
        public String getHeadsName() {
            return prefix + SPLIT + HEADS;
        }
        
        public String getConfirmName() {
            return prefix + SPLIT + CONFIRM;
        }
        
        public Menu categories() {
            return get(getCategoriesName());
        }
        
        public Menu heads() {
            return get(getHeadsName());
        }
        
        public Menu confirm() {
            return get(getConfirmName());
        }
        
        public Menu fromType(InventoryType type) {
            switch (type) {
                case CATEGORY:
                    return categories();
                case HEADS:
                    return heads();
                case CONFIRM:
                    return confirm();
                default:
                    return null;
            }
        }
    }
    
}