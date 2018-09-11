package nl.marido.deluxeheads.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredListener;

import com.mojang.authlib.GameProfile;

import net.sothatsit.blockstore.BlockStoreApi;
import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.volatilecode.ItemNBT;
import nl.marido.deluxeheads.volatilecode.Items;
import nl.marido.deluxeheads.volatilecode.TextureGetter;
import nl.marido.deluxeheads.volatilecode.reflection.nms.BlockPosition;
import nl.marido.deluxeheads.volatilecode.reflection.nms.TileEntitySkull;
import nl.marido.deluxeheads.volatilecode.reflection.nms.World;

public class HeadNamer implements Listener {

	public void registerEvents() {
		Bukkit.getPluginManager().registerEvents(this, DeluxeHeads.getInstance());
	}

	private boolean shouldUseBlockStore() {
		return DeluxeHeads.getMainConfig().shouldUseBlockStore() && DeluxeHeads.isBlockStoreAvailable();
	}

	@SuppressWarnings("deprecation")
	private boolean isHeadsHead(ItemStack item) {
		if (!Items.isSkull(item))
			return false;

		SkullMeta meta = (SkullMeta) item.getItemMeta();
		// This needs to be kept too since it will not work on 1.8 if changed.
		return meta.hasOwner() && meta.getOwner().equals("SpigotHeadPlugin");
	}

	@SuppressWarnings("deprecation")
	private boolean isHeadsHead(Block block) {
		BlockState state = block.getState();
		if (!(state instanceof Skull))
			return false;

		Skull skull = (Skull) state;

		// This needs to be kept too since it will not work on 1.8 if changed.
		return skull.getOwner() != null && skull.getOwner().equals("SpigotHeadPlugin");
	}

	private GameProfile getGameProfile(Block block) {
		World world = new World(block.getWorld());
		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		TileEntitySkull tile = world.getTileEntity(pos).asSkullEntity();

		return tile.getGameProfile();
	}

	private String findHeadName(Block block) {
		if (DeluxeHeads.getMainConfig().shouldUseCacheNames()) {
			GameProfile profile = getGameProfile(block);
			String texture = TextureGetter.findTexture(profile);
			CacheHead head = DeluxeHeads.getCache().findHeadByTexture(texture);

			if (head != null)
				return ChatColor.GRAY + head.getName();
		}

		return ChatColor.GRAY + DeluxeHeads.getMainConfig().getDefaultHeadName();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!DeluxeHeads.getMainConfig().isHeadNamesEnabled() || !shouldUseBlockStore() || !isHeadsHead(e.getItemInHand()))
			return;

		ItemMeta meta = e.getItemInHand().getItemMeta();

		if (!meta.hasDisplayName())
			return;

		BlockStoreApi.setBlockMeta(e.getBlock(), DeluxeHeads.getInstance(), "name", meta.getDisplayName());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if (!DeluxeHeads.getMainConfig().isHeadNamesEnabled())
			return;

		Block block = e.getBlock();

		if (e.getPlayer().getGameMode() == GameMode.CREATIVE || !isHeadsHead(block))
			return;

		// Stop the head item being dropped by the server
		e.setCancelled(true);

		if (shouldUseBlockStore()) {
			BlockStoreApi.retrieveBlockMeta(DeluxeHeads.getInstance(), block, DeluxeHeads.getInstance(), "name", metaValue -> {
				String newName;

				if (metaValue instanceof String) {
					newName = (String) metaValue;
				} else {
					newName = findHeadName(block);
				}

				redropRenamedSkull(block, e.getPlayer(), newName);
			});
		} else {
			redropRenamedSkull(block, e.getPlayer(), findHeadName(block));
		}
	}

	private void redropRenamedSkull(Block block, Player player, String newName) {
		BlockBreakEvent event = new BlockBreakEvent(block, player);

		List<RegisteredListener> listenersToCall = new ArrayList<>();

		for (RegisteredListener listener : BlockBreakEvent.getHandlerList().getRegisteredListeners()) {
			if (!listener.getPlugin().isEnabled() || listener.getListener() instanceof HeadNamer)
				continue;

			listenersToCall.add(listener);
		}

		CountdownRunnable eventResultHandler = new CountdownRunnable(listenersToCall.size(), () -> {
			if (event.isCancelled())
				return;

			GameProfile profile = getGameProfile(block);
			ItemStack drop = ItemNBT.createHead(profile, newName);

			Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(dropLocation, drop);
		});

		for (RegisteredListener listener : listenersToCall) {
			new BlockBreakEventCaller(listener, event, eventResultHandler).scheduleTask();
		}
	}

	private static class BlockBreakEventCaller implements Runnable {

		private final RegisteredListener listener;
		private final BlockBreakEvent event;
		private final CountdownRunnable countdown;

		public BlockBreakEventCaller(RegisteredListener listener, BlockBreakEvent event, CountdownRunnable countdown) {
			this.listener = listener;
			this.event = event;
			this.countdown = countdown;
		}

		public void scheduleTask() {
			Bukkit.getScheduler().scheduleSyncDelayedTask(listener.getPlugin(), this);
		}

		@Override
		public void run() {
			try {
				listener.callEvent(event);
			} catch (EventException exception) {
				DeluxeHeads.severe("There was an exception calling BlockBreakEvent for " + listener.getPlugin().getName());
				exception.printStackTrace();
			} finally {
				countdown.countdown();
			}
		}
	}

	private static class CountdownRunnable {

		private final AtomicInteger countdown;
		private final Runnable runnable;

		public CountdownRunnable(int count, Runnable runnable) {
			this.countdown = new AtomicInteger(count);
			this.runnable = runnable;
		}

		public void countdown() {
			if (countdown.decrementAndGet() != 0)
				return;

			DeluxeHeads.sync(runnable);
		}
	}

}
