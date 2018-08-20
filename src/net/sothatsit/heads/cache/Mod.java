package net.sothatsit.heads.cache;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Mod {

    public enum ModType {

        ADDON(0) {
            @Override
            public Mod read(ObjectInputStream stream) throws IOException {
                return CacheFile.read(stream);
            }
        },

        PATCH(1) {
            @Override
            public Mod read(ObjectInputStream stream) throws IOException {
                return PatchFile.read(stream);
            }
        };

        private final int id;

        private ModType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public abstract Mod read(ObjectInputStream stream) throws IOException;

        public static ModType getById(int id) {
            for(ModType type : ModType.values()) {
                if(type.getId() == id)
                    return type;
            }

            return null;
        }

    }

    public String getName();

    public ModType getType();

    public void write(ObjectOutputStream stream) throws IOException;

    public void applyMod(CacheFile cache);

}
