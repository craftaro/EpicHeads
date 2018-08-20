package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PatchFile implements Mod {

    private final String name;
    private final List<HeadPatch> patches = new ArrayList<>();

    public PatchFile(String name) {
        this(name, Collections.emptyList());
    }

    public PatchFile(String name, List<HeadPatch> patches) {
        this.name = name;
        this.patches.addAll(patches);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Mod.ModType getType() {
        return ModType.PATCH;
    }

    public int getPatchCount() {
        return patches.size();
    }

    public void addPatch(HeadPatch patch) {
        patches.add(patch);
    }

    @Override
    public void applyMod(CacheFile cache) {
        for(HeadPatch patch : patches) {
            patch.applyPatch(cache);
        }
    }

    @Override
    public String toString() {
        return getType() + " {name: \"" + name + "\", patchCount: " + getPatchCount() + "}";
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

    @Override
    public void write(ObjectOutputStream stream) throws IOException {
        stream.writeInt(2);
        stream.writeUTF(name);

        stream.writeInt(patches.size());
        for(HeadPatch patch : patches) {
            patch.write(stream);
        }
    }

    public static PatchFile read(File file) throws IOException {
        if(file.isDirectory())
            throw new IOException("File " + file + " is a directory");

        if(!file.exists())
            throw new IOException("File " + file + " does not exist");

        try(FileInputStream stream = new FileInputStream(file)) {
            return readCompressed(stream);
        }
    }

    public static PatchFile readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static PatchFile readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static PatchFile read(ObjectInputStream stream) throws IOException {
        int version = stream.readInt();

        String name = stream.readUTF();

        int patchCount = stream.readInt();
        List<HeadPatch> patches = new ArrayList<>(patchCount);
        for(int index = 0; index < patchCount; ++index) {
            patches.add(HeadPatch.read(version, stream));
        }

        return new PatchFile(name, patches);
    }

}
