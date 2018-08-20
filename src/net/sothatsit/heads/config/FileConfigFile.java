package net.sothatsit.heads.config;

import java.io.*;

import net.sothatsit.heads.Heads;

import net.sothatsit.heads.util.Checks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileConfigFile extends ConfigFile {

    private YamlConfiguration config;
    private ConfigurationSection defaults;
    private String resourceName;

    public FileConfigFile(String name) {
        this(name, name);
    }
    
    public FileConfigFile(String name, String resourceName) {
        super(name);

        Checks.ensureNonNull(resourceName, "resourceName");

        this.resourceName = resourceName;
    }

    public File getFile() {
        return new File(Heads.getInstance().getDataFolder(), getName());
    }

    @Override
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public void save() {
        File file = getFile();

        try {
            if(!file.exists() && !file.createNewFile())
                throw new IOException("Unable to create config file " + file);

            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reload() {
        config = YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public void copyDefaults() {
        if(getFile().exists())
            return;

        try (InputStream input = Heads.getInstance().getResource(resourceName);
             OutputStream output = new FileOutputStream(getFile())) {

            int read;
            byte[] buffer = new byte[2048];
            while((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConfigurationSection getDefaults() {
        if(defaults == null) {
            InputStream resource = Heads.getInstance().getResource(resourceName);
            InputStreamReader reader = new InputStreamReader(resource);
            defaults = YamlConfiguration.loadConfiguration(reader);
        }

        return defaults;
    }
    
}
