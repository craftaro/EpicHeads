package net.sothatsit.heads.cache.legacy;

import net.sothatsit.heads.config.ConfigFile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LegacyCacheConfig {

    private final ConfigFile configFile;

    private List<LegacyCachedHead> heads = new ArrayList<>();
    private Set<String> addons = new HashSet<>();

    public LegacyCacheConfig(ConfigFile configFile) {
        this.configFile = configFile;

        reload();
    }

    public Set<String> getAddons() {
        return addons;
    }

    public List<LegacyCachedHead> getHeads() {
        return Collections.unmodifiableList(heads);
    }

    public void reload() {
        this.configFile.copyDefaults();
        this.configFile.reload();

        ConfigurationSection config = this.configFile.getConfig();

        this.addons = new HashSet<>(config.getStringList("addons"));

        this.heads.clear();

        // Load all the heads from the legacy config file
        int maxId = 0;
        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key))
                continue;
            
            LegacyCachedHead head = new LegacyCachedHead();

            head.load(config.getConfigurationSection(key));

            if(!head.isValid())
                continue;

            heads.add(head);

            maxId = Math.max(maxId, head.getId());
        }

        // Give IDs to heads that need them
        for(LegacyCachedHead head : heads) {
            if(!head.hasId()) {
                head.setId(++maxId);
            }
        }
    }

}
