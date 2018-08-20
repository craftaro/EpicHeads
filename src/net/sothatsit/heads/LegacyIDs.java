package net.sothatsit.heads;

import net.sothatsit.heads.util.Checks;
import org.bukkit.Material;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LegacyIDs {

    public static final LegacyIDs EMPTY = new LegacyIDs(Collections.emptyMap());

    private final Map<Integer, String> idToType;

    public LegacyIDs(Map<Integer, String> idToType) {
        Checks.ensureNonNull(idToType, "idToType");

        this.idToType = idToType;
    }

    public String fromId(int id) {
        return idToType.get(id);
    }

    public void write(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter osr = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osr)) {

            write(writer);
        }
    }

    public void write(BufferedWriter writer) throws IOException {
        for(Map.Entry<Integer, String> entry : idToType.entrySet()) {
            writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
        }
    }

    public static LegacyIDs create() {
        Map<Integer, String> idToType = new HashMap<>();

        for(Material type : Material.values()) {
            idToType.put(type.getId(), type.name());
        }

        return new LegacyIDs(idToType);
    }

    public static LegacyIDs readResource(String resource) throws IOException {
        try (InputStream is = Heads.getInstance().getResource(resource);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)) {

            return read(reader);
        }
    }

    public static LegacyIDs read(BufferedReader reader) throws IOException {
        Map<Integer, String> idToType = new HashMap<>();

        String line;
        while((line = reader.readLine()) != null) {
            int splitIndex = line.indexOf(':');
            int id = Integer.valueOf(line.substring(0, splitIndex));
            String type = line.substring(splitIndex + 1);
            idToType.put(id, type);
        }

        return new LegacyIDs(idToType);
    }
}
