package net.sothatsit.heads.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class IOUtils {

    public static void writeArray(ObjectOutputStream stream, String[] array) throws IOException {
        Checks.ensureNonNull(stream, "stream");
        Checks.ensureArrayNonNull(array, "array");

        stream.writeInt(array.length);
        for(String element : array) {
            stream.writeUTF(element);
        }
    }

    public static String[] readArray(ObjectInputStream stream) throws IOException {
        Checks.ensureNonNull(stream, "stream");

        int length = stream.readInt();
        String[] array = new String[length];
        for(int index = 0; index < length; ++index) {
            array[index] = stream.readUTF();
        }

        return array;
    }

    public static void writeStringSet(ObjectOutputStream stream, Set<String> set) throws IOException {
        String[] array = set.toArray(new String[set.size()]);

        writeArray(stream, array);
    }

    public static Set<String> readStringSet(ObjectInputStream stream) throws IOException {
        String[] array = readArray(stream);

        return new HashSet<>(Arrays.asList(array));
    }

    public static void writeStringList(ObjectOutputStream stream, List<String> list) throws IOException {
        String[] array = list.toArray(new String[list.size()]);

        writeArray(stream, array);
    }

    public static List<String> readStringList(ObjectInputStream stream) throws IOException {
        String[] array = readArray(stream);

        return new ArrayList<>(Arrays.asList(array));
    }

    public static void writeUUID(ObjectOutputStream stream, UUID uuid) throws IOException {
        stream.writeLong(uuid.getMostSignificantBits());
        stream.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ObjectInputStream stream) throws IOException {
        long mostSignificantBits = stream.readLong();
        long leastSignificantBits = stream.readLong();

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

}
