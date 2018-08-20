package net.sothatsit.heads.volatilecode.reflection;


public class Version {
    
    private static final char[] allowed = "0123456789_".toCharArray();
    public static final Version v1_8 = Version.getVersion("v1_8");
    public static final Version v1_10 = Version.getVersion("v1_10");
    public static final Version v1_13 = Version.getVersion("v1_13");
    
    private int major;
    private int minor;
    private int revision;
    
    public Version(int major, int minor, int revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }
    
    public int getMajor() {
        return major;
    }
    
    public int getMinor() {
        return minor;
    }
    
    public int getRevision() {
        return revision;
    }
    
    public boolean higherThan(Version other) {
        return other.getMajor() < getMajor() || other.getMinor() < getMinor() || other.getRevision() < getRevision();
    }
    
    public static Version getVersion() {
        return getVersion(ReflectionUtils.getServerVersion());
    }
    
    public static Version getVersion(String version) {
        StringBuilder builder = new StringBuilder();
        
        for (char c : version.toCharArray()) {
            if (isAllowed(c)) {
                builder.append(c);
            }
        }
        
        String[] split = builder.toString().split("_");
        
        if (split.length != 2 && split.length != 3) {
            throw new IllegalArgumentException("version is not of the valid type v?_?_R?");
        }
        
        int major = Integer.valueOf(split[0]);
        int minor = Integer.valueOf(split[1]);
        int revision = 0;
        
        if (split.length == 3) {
            revision = Integer.valueOf(split[2]);
        }
        
        return new Version(major, minor, revision);
    }

    public static boolean isAbove(Version version) {
        return getVersion().higherThan(version);
    }

    public static boolean isBelow(Version version) {
        return version.higherThan(getVersion());
    }

    private static boolean isAllowed(char c) {
        for (char ch : allowed) {
            if (ch == c) {
                return true;
            }
        }
        return false;
    }
    
}
