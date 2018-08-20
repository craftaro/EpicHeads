package net.sothatsit.heads.volatilecode.reflection;

public abstract class ReflectObject {
    
    protected final Object handle;
    
    public ReflectObject(Object handle) {
        this.handle = handle;
    }
    
    public Object getHandle() {
        return handle;
    }

    public boolean isNull() {
        return handle == null;
    }
    
}
