package net.sothatsit.heads.volatilecode.injection;

import java.util.concurrent.Executor;

import net.sothatsit.heads.volatilecode.reflection.Version;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CraftMetaSkullSub1;
import net.sothatsit.heads.volatilecode.reflection.nms.TileEntitySkull;

import javax.annotation.Nonnull;

public class ProtocolHackFixer {
    
    public static void fix() {
        if (Version.v1_8.higherThan(Version.getVersion())) {
            injectTileEntitySkullExecutor();
        }
    }
    
    private static void injectTileEntitySkullExecutor() {
        TileEntitySkull.setExecutor(new InterceptExecutor(TileEntitySkull.getExecutor()));
    }
    
    private static class InterceptExecutor implements Executor {
        
        private final Executor handle;
        
        private InterceptExecutor(Executor handle) {
            this.handle = handle;
        }
        
        @Override
        public void execute(@Nonnull Runnable command) {
            if (command.getClass().equals(CraftMetaSkullSub1.CraftMetaSkullSub1Class)) {
                CraftMetaSkullSub1 skull = new CraftMetaSkullSub1(command);
                
                if (skull.getMeta().getProfile().getName() == null)
                    return;
            }
            
            handle.execute(command);
        }
        
    }
    
}
