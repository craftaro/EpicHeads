package net.sothatsit.heads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {

    private static final String versionURL = "https://api.spigotmc.org/legacy/update.php?resource=13402";

    public static String getCurrentVersion() {
        return Heads.getInstance().getDescription().getVersion();
    }

    public static String getLatestVersion() throws IOException {
        URL url = new URL(versionURL);

        try(InputStream is = url.openStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr)) {

            return reader.readLine();
        }
    }

    public static boolean isNewerVersion(String latestVersion) {
        return isEarlierVersion(getCurrentVersion(), latestVersion);
    }

    private static int[] decodeVersion(String version) {
        String[] split = version.split("\\.");
        int[] pieces = new int[split.length];

        for(int index = 0; index < split.length; ++index) {
            try {
                pieces[index] = Integer.valueOf(split[index]);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("Invalid version " + version);
            }
        }

        return pieces;
    }

    private static boolean isEarlierVersion(String currentVersion, String latestVersion) {
        int[] current = decodeVersion(currentVersion);
        int[] latest = decodeVersion(latestVersion);

        int length = Math.min(current.length, latest.length);
        for(int index = 0; index < length; ++index) {
            if(current[index] > latest[index])
                return false;

            if(current[index] < latest[index])
                return true;
        }

        return false;
    }

}
