package net.sothatsit.heads.oldmenu;

import java.lang.reflect.Constructor;

import net.sothatsit.heads.oldmenu.mode.InvMode;

public enum InventoryType {

    CATEGORY(CategorySelectMenu.class),
    HEADS(HeadMenu.class),
    CONFIRM(ConfirmMenu.class);
    
    private Class<? extends AbstractModedInventory> clazz;
    
    private InventoryType(Class<? extends AbstractModedInventory> clazz) {
        this.clazz = clazz;
    }
    
    public Class<? extends AbstractModedInventory> getMenuClass() {
        return clazz;
    }
    
    public AbstractModedInventory createMenu(InvMode invmode, Object... arguments) {
        try {
            Object[] args = new Object[arguments.length + 1];
            
            System.arraycopy(arguments, 0, args, 1, arguments.length);
            args[0] = invmode;
            
            Class<?>[] argTypes = new Class<?>[args.length];
            
            for (int i = 0; i < argTypes.length; i++) {
                argTypes[i] = (args[i] == null ? null : args[i].getClass());
            }
            
            outer: for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.getParameterTypes().length != args.length) {
                    continue;
                }
                
                Class<?>[] params = constructor.getParameterTypes();
                for (int i = 0; i < argTypes.length; i++) {
                    if (argTypes[i] == null) {
                        if (!Object.class.isAssignableFrom(argTypes[i])) {
                            continue outer;
                        }
                        continue;
                    }
                    
                    if (!params[i].isAssignableFrom(argTypes[i])) {
                        continue outer;
                    }
                }
                
                return (AbstractModedInventory) constructor.newInstance(args);
            }
            
            throw new IllegalArgumentException(clazz + " does not contain a valid constructor for the provided arguments");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
