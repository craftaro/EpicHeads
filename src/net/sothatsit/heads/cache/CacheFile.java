package net.sothatsit.heads.cache;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Search;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.IOUtils;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class CacheFile implements Mod {

    private final String name;
    private final Set<String> mods = new HashSet<>();
    private final List<CacheHead> heads = new ArrayList<>();
    private final Map<Integer, CacheHead> headsById = new HashMap<>();
    private final Map<String, CacheHead> headsByTexture = new HashMap<>();
    private final Map<String, List<CacheHead>> categories = new HashMap<>();

    public CacheFile(String name) {
        this(name, Collections.emptySet(), Collections.emptyList());
    }

    public CacheFile(String name, Set<String> mods, Iterable<CacheHead> heads) {
        Checks.ensureNonNull(name, "name");
        Checks.ensureNonNull(mods, "mods");
        Checks.ensureNonNull(heads, "heads");

        this.name = name;
        this.mods.addAll(mods);

        addHeads(heads);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ModType getType() {
        return ModType.ADDON;
    }

    public int getHeadCount() {
        return heads.size();
    }

    public List<CacheHead> getHeads() {
        return Collections.unmodifiableList(heads);
    }

    public String resolveCategoryName(String category) {
        for(String name : categories.keySet()) {
            if(name.equalsIgnoreCase(category))
                return name;
        }

        return category;
    }

    public Set<String> getCategories() {
        return Collections.unmodifiableSet(categories.keySet());
    }

    public List<CacheHead> getCategoryHeads(String category) {
        category = resolveCategoryName(category);

        List<CacheHead> list = categories.getOrDefault(category, Collections.emptyList());

        Collections.sort(list);

        return Collections.unmodifiableList(list);
    }

    public List<CacheHead> searchHeads(String query) {
        return Search.searchHeads(query, heads, 0.4d);
    }

    public void searchHeadsAsync(String query, Consumer<List<CacheHead>> onResult) {
        List<CacheHead> headsCopy = new ArrayList<>(heads);

        Heads.async(() -> {
            List<CacheHead> matches = Search.searchHeads(query, headsCopy, 0.4d);

            Heads.sync(() -> onResult.accept(matches));
        });
    }

    public CacheHead findHead(int id) {
        return headsById.get(id);
    }

    public CacheHead findHeadByTexture(String texture) {
        return headsByTexture.get(texture);
    }

    public List<CacheHead> findHeads(UUID uniqueId) {
        List<CacheHead> matches = new ArrayList<>();

        for(CacheHead head : heads) {
            if(!head.getUniqueId().equals(uniqueId))
                continue;

            matches.add(head);
        }

        return matches;
    }

    public CacheHead getRandomHead(Random random) {
        return heads.get(random.nextInt(heads.size()));
    }

    public void addHeads(Iterable<CacheHead> heads) {
        for(CacheHead head : heads) {
            addHead(head);
        }
    }

    private int getMaxId() {
        int max = -1;

        for(CacheHead head : heads) {
            max = Math.max(max, head.getId());
        }

        return max;
    }

    public void addHead(CacheHead head) {
        String category = resolveCategoryName(head.getCategory());

        head = head.copyWithCategory(category);
        head.setId(getMaxId() + 1);

        heads.add(head);
        headsById.put(head.getId(), head);
        headsByTexture.put(head.getTexture(), head);
        categories.computeIfAbsent(category, c -> new ArrayList<>()).add(head);
    }

    public void removeHead(CacheHead head) {
        String category = resolveCategoryName(head.getCategory());

        heads.remove(head);
        headsById.remove(head.getId(), head);
        headsByTexture.remove(head.getTexture(), head);
        categories.compute(category, (key, categoryHeads) -> {
            if(categoryHeads == null)
                return null;

            categoryHeads.remove(head);

            return (categoryHeads.size() > 0 ? categoryHeads : null);
        });
    }

    @Override
    public void applyMod(CacheFile cache) {
        cache.addHeads(heads);
    }

    public boolean hasMod(String mod) {
        return mods.contains(mod);
    }

    public void installMod(Mod mod) {
        if(hasMod(mod.getName()))
            return;

        mods.add(mod.getName());
        mod.applyMod(this);
    }

    @Override
    public String toString() {
        return getType() + " {name: \"" + name + "\", headCount: " + getHeadCount() + "}";
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

        IOUtils.writeStringSet(stream, mods);

        stream.writeInt(heads.size());
        for(CacheHead head : heads) {
            head.write(stream);
        }
    }

    public static CacheFile read(File file) throws IOException {
        if(file.isDirectory())
            throw new IOException("File " + file + " is a directory");

        if(!file.exists())
            throw new IOException("File " + file + " does not exist");

        try(FileInputStream stream = new FileInputStream(file)) {
            return readCompressed(stream);
        }
    }

    public static CacheFile readResource(String resource) throws IOException {
        try(InputStream stream = Heads.getInstance().getResource(resource)) {
            return readCompressed(stream);
        }
    }

    public static CacheFile readCompressed(InputStream is) throws IOException {
        try(GZIPInputStream zis = new GZIPInputStream(is);
            ObjectInputStream stream = new ObjectInputStream(zis)) {

            return read(stream);
        }
    }

    public static CacheFile read(ObjectInputStream stream) throws IOException {
        int version = stream.readInt();

        switch(version) {
            case 2:
                return readVersion2(stream);
            case 1:
                return readVersion1(stream);
            default:
                throw new UnsupportedOperationException("Unknown cache file version " + version);
        }
    }

    private static CacheFile readVersion2(ObjectInputStream stream) throws IOException {
        String name = stream.readUTF();

        Set<String> mods = IOUtils.readStringSet(stream);

        int headCount = stream.readInt();
        List<CacheHead> heads = new ArrayList<>(headCount);
        for(int index = 0; index < headCount; ++index) {
            heads.add(CacheHead.read(stream));
        }

        return new CacheFile(name, mods, heads);
    }

    private static CacheFile readVersion1(ObjectInputStream stream) throws IOException {
        String name = stream.readUTF();

        Set<String> mods = new HashSet<>();

        mods.addAll(IOUtils.readStringSet(stream));
        mods.addAll(IOUtils.readStringSet(stream));

        int headCount = stream.readInt();
        List<CacheHead> heads = new ArrayList<>(headCount);
        for(int index = 0; index < headCount; ++index) {
            heads.add(CacheHead.read(stream));
        }

        return new CacheFile(name, mods, heads);
    }

}
