package net.sothatsit.heads.cache.legacy;

import org.bukkit.configuration.ConfigurationSection;

public class LegacyCachedHead {

    private int id = -1;
    private String category = "";
    private String name = "";
    private String texture = "";
    private String[] tags = {};
    private double cost = -1;
    
    public boolean isValid() {
        return !this.name.isEmpty();
    }

    public boolean hasId() {
        return this.id > 0;
    }
    
    public int getId() {
        return this.id;
    }
    
    protected void setId(int id) {
        this.id = id;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTexture() {
        return this.texture;
    }

    public String[] getTags() {
        return this.tags;
    }

    public double getCost() {
        return cost;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void load(ConfigurationSection section) {
        this.id = section.getInt("id", -1);
        this.category = section.getString("category", "none");
        this.name = section.getString("name", "");
        this.texture = section.getString("texture", "");
        this.cost = section.getDouble("cost", -1d);

        if(section.isSet("tags") && section.isString("tags")) {
            this.tags = new String[] {section.getString("tags")};
        } else if(section.isSet("tags") && section.isList("tags")) {
            this.tags = section.getStringList("tags").toArray(new String[0]);
        }
    }

}
