package net.sothatsit.heads.patches;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.sothatsit.heads.volatilecode.reflection.Version;

public class HeadPlace implements Listener {

	// TODO: Patch the issue reported by BlackGamer000 on 1.8.
	// register event

	@EventHandler
	public void onHeadPlace(BlockPlaceEvent event) {
		if (Version.isBelow(Version.v1_8)) {
			// Need to find a fix for the texures on 1.8
		}
	}

}
