package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class ModsFile {

    private final List<Mod> mods = new ArrayList<>();

    public ModsFile() {
        this(Collections.emptyList());
    }

    public ModsFile(List<Mod> mods) {
        mods.forEach(this::addMod);
    }

    public Set<String> getModNames() {
        return mods.stream()
                .map(Mod::getName)
                .collect(Collectors.toSet());
    }

    public void addMod(Mod newMod) {
        for(Mod mod : mods) {
            if(mod.getName().equalsIgnoreCase(newMod.getName()))
                throw new IllegalArgumentException("There is already a mod with the name " + mod.getName());
        }

        mods.add(newMod);
    }

    public int installMods(CacheFile cache) {
        int headsBefore = cache.getHeadCount();

        mods.forEach(cache::installMod);

        return cache.getHeadCount() - headsBefore;
    }

    public void write(File file) throws IOException {
        if(file.isDirectory())
            throw new IOException("File " + file + " is a directory");

        if (!file.exists() && !file.createNewFile())
            throw new IOException("Unable to create file " + file);

        try(FileOutputStream stream = new FileOutputStream(file)) {
            writeCompressed(stream);
        }
    }

    public void writeCompressed(OutputStream os) throws IOException {
        try(GZIPOutputStream zos = new GZIPOutputStream(os);
            ObjectOutputStream stream = new ObjectOutputStream(zos)) {

            write(stream);

            stream.flush();
        }
    }

    public void write(ObjectOutputStream stream) throws IOException {
        ModsFileHeader header = new ModsFileHeader(getModNames());

        header.write(stream);

        stream.writeInt(mods.size());
        for(Mod mod : mods) {
            stream.writeInt(mod.getType().getId());

            mod.write(stream);
        }
    }

    public static ModsFile readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static ModsFile readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static ModsFile read(ObjectInputStream stream) throws IOException {
        ModsFileHeader header = ModsFileHeader.read(stream);

        switch(header.getVersion()) {
            case 2:
                return readVersion2(stream);
            case 1:
                return readVersion1(stream);
            default:
                throw new UnsupportedOperationException("Unknown mods file version " + header.getVersion());
        }
    }

    private static ModsFile readVersion2(ObjectInputStream stream) throws IOException {
        int modCount = stream.readInt();

        List<Mod> mods = new ArrayList<>(modCount);
        for(int index = 0; index < modCount; ++index) {
            int modTypeId = stream.readInt();
            Mod.ModType modType = Mod.ModType.getById(modTypeId);

            if(modType == null)
                throw new UnsupportedOperationException("Unknown mod type " + modTypeId);

            mods.add(modType.read(stream));
        }

        return new ModsFile(mods);
    }

    private static ModsFile readVersion1(ObjectInputStream stream) throws IOException {
        int addonCount = stream.readInt();

        List<Mod> addons = new ArrayList<>(addonCount);
        for(int index = 0; index < addonCount; ++index) {
            addons.add(CacheFile.read(stream));
        }

        return new ModsFile(addons);
    }

}
