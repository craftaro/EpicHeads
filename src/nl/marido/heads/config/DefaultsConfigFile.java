package nl.marido.heads.config;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import nl.marido.heads.Heads;

public class DefaultsConfigFile extends ConfigFile {

    private ConfigurationSection config;
    
    public DefaultsConfigFile(String name) {
        super(name);
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Cannot save a DefaultsConfigFile.");
    }

    @Override
    public void reload() {
        InputStream resource = Heads.getInstance().getResource(getName());
        InputStreamReader reader = new InputStreamReader(resource);
        config = YamlConfiguration.loadConfiguration(reader);
    }

    @Override
    public void copyDefaults() {
        throw new UnsupportedOperationException("Cannot save a DefaultsConfigFile.");
    }

    @Override
    public ConfigurationSection getDefaults() {
        return config;
    }


    
}
