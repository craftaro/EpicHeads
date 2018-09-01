package nl.marido.heads.patches;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import nl.marido.heads.volatilecode.reflection.Version;

public class HeadPlace implements Listener {

	@EventHandler
	public void headPlace(BlockPlaceEvent event) {
		if (Version.getVersion() == Version.v1_8) {

		}
	}
}
