package net.sothatsit.heads.cache;

import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.IOUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public final class HeadPatch {

    private final UUID uniqueId;

    private boolean category = false;
    private String fromCategory = null;
    private String toCategory = null;

    private boolean tags = false;
    private List<String> fromTags = null;
    private List<String> toTags = null;

    private boolean cost = false;
    private double fromCost = -1;
    private double toCost = -1;

    public HeadPatch(CacheHead head) {
        this(head.getUniqueId());
    }

    public HeadPatch(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isEmpty() {
        return !category && !tags && !cost;
    }

    public HeadPatch withCategory(String from, String to) {
        Checks.ensureNonNull(from, "from");
        Checks.ensureNonNull(to, "to");

        if(from.equals(to)) {
            this.category = false;
            return this;
        }

        this.category = true;
        this.fromCategory = from;
        this.toCategory = to;

        return this;
    }

    public HeadPatch withTags(List<String> from, List<String> to) {
        Checks.ensureNonNull(from, "from");
        Checks.ensureNonNull(to, "to");

        if(new HashSet<>(from).equals(new HashSet<>(to))) {
            this.tags = false;
            return this;
        }

        this.tags = true;
        this.fromTags = from;
        this.toTags = to;

        return this;
    }

    public HeadPatch withCost(double from, double to) {
        if(from == to) {
            this.cost = false;
            return this;
        }

        this.cost = true;
        this.fromCost = from;
        this.toCost = to;

        return this;
    }

    public void applyPatch(CacheFile cache) {
        for(CacheHead head : cache.findHeads(uniqueId)) {
            applyPatch(cache, head);
        }
    }

    public void applyPatch(CacheFile cache, CacheHead head) {
        if(category && head.getCategory().equalsIgnoreCase(fromCategory)) {
            cache.removeHead(head);

            head = head.copyWithCategory(toCategory);

            cache.addHead(head);
        }

        if(tags && head.getTags().equals(fromTags)) {
            head.setTags(toTags);
        }

        if(cost && head.getRawCost() == fromCost) {
            head.setCost(toCost);
        }
    }

    public static HeadPatch createPatch(CacheHead original, CacheHead updated) {
        HeadPatch patch = new HeadPatch(original);

        patch.withCost(original.getRawCost(), updated.getRawCost());
        patch.withCategory(original.getCategory(), updated.getCategory());
        patch.withTags(original.getTags(), updated.getTags());

        return (!patch.isEmpty() ? patch : null);
    }

    public void write(ObjectOutputStream stream) throws IOException {
        IOUtils.writeUUID(stream, uniqueId);

        stream.writeBoolean(category);
        if(category) {
            stream.writeUTF(fromCategory);
            stream.writeUTF(toCategory);
        }

        stream.writeBoolean(tags);
        if(tags) {
            IOUtils.writeStringList(stream, fromTags);
            IOUtils.writeStringList(stream, toTags);
        }

        stream.writeBoolean(cost);
        if(cost) {
            stream.writeDouble(fromCost);
            stream.writeDouble(toCost);
        }
    }

    public static HeadPatch read(int version, ObjectInputStream stream) throws IOException {
        switch(version) {
            case 1:
                return readVersion1(stream);
            case 2:
                return readVersion2(stream);
            default:
                throw new UnsupportedOperationException("Unknown patch file version " + version);
        }
    }

    public static HeadPatch readVersion2(ObjectInputStream stream) throws IOException {
        HeadPatch patch = readVersion1(stream);

        boolean cost = stream.readBoolean();
        if(cost) {
            double from = stream.readDouble();
            double to = stream.readDouble();

            patch.withCost(from, to);
        }

        return patch;
    }

    public static HeadPatch readVersion1(ObjectInputStream stream) throws IOException {
        UUID uniqueId = IOUtils.readUUID(stream);

        HeadPatch patch = new HeadPatch(uniqueId);

        boolean category = stream.readBoolean();
        if(category) {
            String from = stream.readUTF();
            String to = stream.readUTF();

            patch.withCategory(from, to);
        }

        boolean tags = stream.readBoolean();
        if(tags) {
            List<String> from = IOUtils.readStringList(stream);
            List<String> to = IOUtils.readStringList(stream);

            patch.withTags(from, to);
        }

        return patch;
    }

}
