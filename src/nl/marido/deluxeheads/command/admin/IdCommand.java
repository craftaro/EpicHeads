package nl.marido.deluxeheads.command.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.cache.CacheHead;
import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.volatilecode.ItemNBT;
import nl.marido.deluxeheads.volatilecode.Items;

public class IdCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getIdCommand();
	}

	@Override
	public String getPermission() {
		return "deluxeheads.id";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.Id.help();
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Lang.Command.Errors.mustBePlayer().send(sender);
			return true;
		}

		Player player = (Player) sender;

		if (args.length != 1) {
			sendInvalidArgs(sender);
			return true;
		}

		ItemStack hand = player.getInventory().getItemInHand();
		if (!Items.isSkull(hand)) {
			Lang.Command.Id.holdSkull().send(sender);
			return true;
		}

		String texture = ItemNBT.getTextureProperty(hand);
		CacheHead head = DeluxeHeads.getCache().findHeadByTexture(texture);
		if (head == null) {
			ItemMeta meta = hand.getItemMeta();
			String name = ChatColor.stripColor(meta.hasDisplayName() ? meta.getDisplayName() : "");
			Lang.Command.Id.unknownHead(name).send(sender);
			return true;
		}

		Lang.Command.Id.foundID(head.getName(), head.getId()).send(sender);
		return true;
	}

}
