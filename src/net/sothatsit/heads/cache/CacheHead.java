package net.sothatsit.heads.cache;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.IOUtils;
import net.sothatsit.heads.volatilecode.ItemNBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class CacheHead implements Comparable<CacheHead> {

    private int id;
    private String name;
    private final String category;
    private final String texture;
    private String textureURL;
    private UUID uniqueId;
    private final List<String> tags = new ArrayList<>();
    private double cost;

    public CacheHead(String name, String category, String texture) {
        this(-1, name, category, texture, Collections.emptyList(), -1d);
    }

    public CacheHead(String name, String category, String texture, String... tags) {
        this(-1, name, category, texture, Arrays.asList(tags), -1d);
    }

    public CacheHead(int id, String name, String category, String texture, List<String> tags, double cost) {
        Checks.ensureNonNull(name, "name");
        Checks.ensureNonNull(category, "category");
        Checks.ensureNonNull(texture, "texture");
        Checks.ensureNonNull(tags, "tags");

        this.id = id;
        this.name = name;
        this.category = category;
        this.texture = texture;
        this.textureURL = null;
        this.uniqueId = null;
        this.tags.addAll(tags);
        this.cost = cost;
    }

    public CacheHead copyWithCategory(String category) {
        return new CacheHead(id, name, category, texture, tags, cost);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPermission() {
        return Heads.getCategoryPermission(category);
    }

    public String getTexture() {
        return texture;
    }

    public String getTextureURL() {
        if(textureURL == null) {
            textureURL = extractTextureURL(texture);
        }

        return textureURL;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean hasCost() {
        return cost >= 0;
    }

    public double getCost() {
        return (hasCost() ? cost : Heads.getMainConfig().getCategoryCost(category));
    }

    public double getRawCost() {
        return cost;
    }

    public UUID getUniqueId() {
        if(uniqueId == null) {
            uniqueId = UUID.nameUUIDFromBytes(getTextureURL().getBytes(StandardCharsets.UTF_8));
        }

        return uniqueId;
    }

    public Placeholder[] getPlaceholders(Player player) {
        return new Placeholder[] {
                new Placeholder("%name%", name),
                new Placeholder("%cost%", Lang.Currency.format(player, getCost())),
                new Placeholder("%category%", category),
                new Placeholder("%id%", Integer.toString(id))
        };
    }

    public ItemStack getItemStack() {
        return ItemNBT.createHead(this, null);
    }

    public ItemStack getItemStack(String name) {
        return ItemNBT.createHead(this, name);
    }

    public ItemStack addTexture(ItemStack itemStack) {
        return ItemNBT.applyHead(this, itemStack);
    }

    protected void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        Checks.ensureNonNull(name, "name");

        this.name = name;
    }

    public void setTags(List<String> tags) {
        Checks.ensureNonNull(tags, "tags");

        this.tags.clear();
        this.tags.addAll(tags);
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void write(ObjectOutputStream stream) throws IOException {
        stream.writeInt(id);
        stream.writeUTF(name);
        stream.writeUTF(category);
        stream.writeUTF(texture);
        IOUtils.writeStringList(stream, tags);
        stream.writeDouble(cost);
    }

    public static CacheHead read(ObjectInputStream stream) throws IOException {
        int id = stream.readInt();
        String name = stream.readUTF();
        String category = stream.readUTF();
        String texture = stream.readUTF();
        List<String> tags = IOUtils.readStringList(stream);
        double cost = stream.readDouble();

        return new CacheHead(id, name, category, texture, tags, cost);
    }

    @Override
    public int compareTo(@Nonnull CacheHead otherHead) {
        String otherName = otherHead.getName();

        if(name.length() > 1 && otherName.length() <= 1)
            return 1;

        if(name.length() <= 1 && otherName.length() > 1)
            return -1;

        if(name.length() == 1 && otherName.length() == 1) {
            List<String> otherTags = otherHead.getTags();

            int length = Math.min(tags.size(), otherTags.size());

            for(int index = 0; index < length; ++index) {
                int compare = tags.get(index).compareTo(otherTags.get(index));

                if(compare != 0)
                    return compare;
            }

            if(tags.size() > 0 && otherTags.size() == 0)
                return -1;

            if(tags.size() == 0 && otherTags.size() > 0)
                return 1;
        }

        return name.compareTo(otherName);
    }

    public static String extractTextureURL(String texture) {
        try {
            String decoded = new String(Base64.getDecoder().decode(texture));
            JsonObject json = new JsonParser().parse(decoded).getAsJsonObject();
            JsonObject textures = json.getAsJsonObject("textures");
            JsonObject skin = textures.getAsJsonObject("SKIN");
            return skin.get("url").getAsString();
        } catch(Exception e) {
            throw new RuntimeException("Unable to get the texture URL of texture " + texture, e);
        }
    }

}
