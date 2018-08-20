package net.sothatsit.heads.cache.legacy;

import net.sothatsit.heads.cache.CacheFile;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.DefaultsConfigFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CacheFileConverter {

    public static CacheFile convertToCacheFile(String name, LegacyCacheConfig config) {
        Set<String> addons = new HashSet<>(config.getAddons());

        addons.add("original");

        List<CacheHead> heads = config.getHeads().stream()
                .map(CacheFileConverter::convertToCacheHead)
                .collect(Collectors.toList());

        return new CacheFile(name, addons, heads);
    }

    public static CacheHead convertToCacheHead(LegacyCachedHead head) {
        int id = head.getId();
        String name = head.getName();
        String texture = head.getTexture();
        String category = head.getCategory();
        List<String> tags = Arrays.asList(head.getTags());
        double cost = head.getCost();

        return new CacheHead(id, name, category, texture, tags, cost);
    }

    public static void convertResource(String name, String resource, File file) throws IOException {
        LegacyCacheConfig addon = new LegacyCacheConfig(new DefaultsConfigFile(resource));

        convertToCacheFile(name, addon).write(file);
    }

}
