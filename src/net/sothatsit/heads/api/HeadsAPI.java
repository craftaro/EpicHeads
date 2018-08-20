package net.sothatsit.heads.api;

import com.google.common.collect.ImmutableList;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.volatilecode.TextureGetter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class HeadsAPI {

    public static class Head {

        private final CacheHead head;

        private Head(CacheHead head) {
            Checks.ensureNonNull(head, "head");

            this.head = head;
        }

        public int getId() {
            return head.getId();
        }

        public String getName() {
            return head.getName();
        }

        public String getCategory() {
            return head.getCategory();
        }

        public double getCost() {
            return head.getCost();
        }

        public ItemStack getItem() {
            return head.getItemStack();
        }

        public ItemStack getItem(String displayName) {
            return head.getItemStack(displayName);
        }

        private static Head fromCacheHead(CacheHead head) {
            return (head == null ? null : new Head(head));
        }

        private static Head fromNameAndTexture(String name, String texture) {
            return (texture == null ? null : fromCacheHead(new CacheHead(name, "HeadsAPI", texture)));
        }

        private static List<Head> fromCacheHeads(List<CacheHead> heads) {
            ImmutableList.Builder<Head> converted = ImmutableList.builder();

            for(CacheHead head : heads) {
                converted.add(Head.fromCacheHead(head));
            }

            return converted.build();
        }

    }

    public static Head getHead(int id) {
        CacheHead head = Heads.getCache().findHead(id);

        if(head == null)
            return null;

        return new Head(head);
    }

    @Deprecated
    public static List<Head> searchHeads(String query) {
        List<CacheHead> search = Heads.getCache().searchHeads(query);

        return Head.fromCacheHeads(search);
    }

    public static void searchHeads(String query, Consumer<List<Head>> onResult) {
        Heads.getCache().searchHeadsAsync(query, heads -> {
            onResult.accept(Head.fromCacheHeads(heads));
        });
    }

    public static Set<String> getCategories() {
        return Heads.getCache().getCategories();
    }

    public static List<Head> getCategoryHeads(String category) {
        List<CacheHead> categoryHeads = Heads.getCache().getCategoryHeads(category);

        return Head.fromCacheHeads(categoryHeads);
    }

    public static List<Head> getAllHeads() {
        List<CacheHead> heads = Heads.getCache().getHeads();

        return Head.fromCacheHeads(heads);
    }

    public static void downloadHead(String playerName, Consumer<Head> consumer) {
        TextureGetter.getTexture(playerName, (texture) -> {
            consumer.accept(Head.fromNameAndTexture(playerName, texture));
        });
    }

}
