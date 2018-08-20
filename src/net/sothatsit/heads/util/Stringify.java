package net.sothatsit.heads.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Stringify {

    public static String capitalise(String string) {
        boolean capitalise = true;

        char[] chars = string.toCharArray();

        for(int index = 0; index < chars.length; ++index) {
            if(Character.isSpaceChar(chars[index])) {
                capitalise = true;
            } else if(capitalise) {
                chars[index] = Character.toUpperCase(chars[index]);
                capitalise = false;
            }
        }

        return new String(chars);
    }

    public static String indent(String string) {
        StringBuilder indented = new StringBuilder("\t");

        int blockStart = 0;

        char[] chars = string.toCharArray();
        for(int index = 0; index < chars.length; ++index) {
            if(chars[index] != '\n')
                continue;

            indented.append(string, blockStart, index + 1).append('\t');
            blockStart = index + 1;
        }

        return indented.append(string, blockStart, string.length()).toString();
    }

    public static String objectToString(Object object) {
        if(object == null)
            return "null";

        Class<?> clazz = object.getClass();

        if(object instanceof ItemStack)
            return itemToString((ItemStack) object);

        if(object instanceof Player)
            return playerToString((Player) object);

        if(object instanceof Inventory)
            return inventoryToString((Inventory) object);

        if(object instanceof String)
            return quoteString((String) object);

        if(clazz.isArray())
            return arrayToString(object);

        if(object instanceof Iterable<?>)
            return iterableToString((Iterable<?>) object);

        return object.toString();
    }

    public static String iterableToString(Iterable<?> iterable) {
        Checks.ensureNonNull(iterable, "iterable");

        List<Object> values = new ArrayList<>();

        iterable.forEach(values::add);

        return arrayToString(values.toArray());
    }

    public static String arrayToString(Object array) {
        Checks.ensureNonNull(array, "array");

        Class<?> clazz = array.getClass();

        Checks.ensureTrue(clazz.isArray(), "array must be an array");

        StringBuilder builder = new StringBuilder();

        builder.append("[");

        int length = Array.getLength(array);
        for(int index = 0; index < length; ++index) {
            if(index != 0) {
                builder.append(", ");
            }

            Object value = Array.get(array, index);

            builder.append(objectToString(value));
        }

        builder.append("]");

        return builder.toString();
    }

    public static String quoteString(String string) {
        Checks.ensureNonNull(string, "string");

        return "\"" + string + "\"";
    }

    public static String itemToString(ItemStack item) {
        Checks.ensureNonNull(item, "item");

        ItemMeta meta = item.getItemMeta();

        Builder properties = builder();
        {
            properties.entry("type", item.getType());

            if(item.getDurability() != 0) {
                properties.entry("data", item.getDurability());
            }

            if(item.getAmount() != 1) {
                properties.entry("amount", item.getAmount());
            }

            if(meta.hasDisplayName()) {
                properties.entry("name", meta.getDisplayName());
            }

            if(meta.hasLore()) {
                properties.entry("lore", meta.getLore());
            }

            if(meta.hasEnchants()) {
                properties.entry("enchanted", true);
            }
        }
        return properties.toString();
    }

    public static String playerToString(Player player) {
        Checks.ensureNonNull(player, "player");

        return builder()
                .entry("name", player.getName())
                .entry("uuid", player.getUniqueId()).toString();
    }

    public static String inventoryToString(Inventory inventory) {
        return builder()
                .entry("name", inventory.getName())
                .entry("size", inventory.getSize()).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String previous;
        private final List<String> keys = new ArrayList<>();
        private final List<Object> values = new ArrayList<>();

        private Builder() {

        }

        public Builder previous(Object previous) {
            Checks.ensureNonNull(previous, "previous");

            return previous(Objects.toString(previous));
        }

        public Builder previous(String previous) {
            Checks.ensureNonNull(previous, "previous");

            // Remove curly brackets
            if(previous.length() >= 2
                    && previous.charAt(0) == '{'
                    && previous.charAt(previous.length() - 1) == '}') {

                this.previous = previous.substring(1, previous.length() - 1);
            } else {
                this.previous = previous;
            }

            return this;
        }

        public Builder entry(String key, Object value) {
            Checks.ensureNonNull(key, "key");

            keys.add(key);
            values.add(value);

            return this;
        }

        @Override
        public String toString() {
            StringBuilder properties = new StringBuilder("{");

            boolean first = true;

            if(previous != null) {
                properties.append(previous);
                first = false;
            }

            for(int index = 0; index < keys.size(); ++index) {
                String key = keys.get(index);
                Object value = values.get(index);

                if(first) {
                    first = false;
                } else {
                    properties.append(",");
                }

                properties.append('\n').append(indent(key + ": " + objectToString(value)));
            }

            if(!first) {
                properties.append('\n');
            }

            return properties.append("}").toString();
        }

    }

}
