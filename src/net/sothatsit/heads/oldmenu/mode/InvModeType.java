package net.sothatsit.heads.oldmenu.mode;

import org.bukkit.entity.Player;

public enum InvModeType {

    GET(GetMode.class),
    REMOVE(RemoveMode.class),
    RENAME(RenameMode.class),
    COST(CostMode.class),
    CATEGORY_COST(CategoryCostMode.class),
    CATEGORY_COST_REMOVE(CategoryCostRemoveMode.class),
    ID(IdMode.class);
    
    private Class<? extends InvMode> clazz;
    
    private InvModeType(Class<? extends InvMode> clazz) {
        this.clazz = clazz;
    }
    
    public Class<? extends InvMode> getInvModeClass() {
        return clazz;
    }
    
    public InvMode open(Player player) {
        try {
            return clazz.getConstructor(Player.class).newInstance(player);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
