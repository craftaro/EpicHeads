package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.util.IOUtils;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class ModsFileHeader {

    private final int version;
    private final Set<String> modNames = new HashSet<>();

    public ModsFileHeader(Set<String> modNames) {
        this(2, modNames);
    }

    public ModsFileHeader(int version, Set<String> modNames) {
        this.version = version;
        this.modNames.addAll(modNames);
    }

    public int getVersion() {
        return version;
    }

    public Set<String> getModNames() {
        return modNames;
    }

    public int getUninstalledMods(CacheFile cache) {
        int newMods = 0;

        for(String mod : modNames) {
            if(cache.hasMod(mod))
                continue;

            ++newMods;
        }

        return newMods;
    }

    public void write(ObjectOutputStream stream) throws IOException {
        stream.writeInt(2);

        IOUtils.writeStringSet(stream, modNames);
    }

    public static ModsFileHeader readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static ModsFileHeader readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static ModsFileHeader read(ObjectInputStream stream) throws IOException {
        int version = stream.readInt();

        switch(version) {
            case 2:
            case 1:
                Set<String> modNames = IOUtils.readStringSet(stream);

                return new ModsFileHeader(version, modNames);
            default:
                throw new UnsupportedOperationException("Unknown mods file version " + version);
        }
    }

}
