package nl.marido.deluxeheads.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.oldmenu.mode.CategoryCostMode;
import nl.marido.deluxeheads.oldmenu.mode.InvModeType;

public class CategoryCostCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getCategoryCostCommand();
	}

	@Override
	public String getPermission() {
		return "deluxeheads.category-cost";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.CategoryCost.help();
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Lang.Command.Errors.mustBePlayer().send(sender);
			return true;
		}
		if (args.length != 2) {
			sendInvalidArgs(sender);
			return true;
		}
		if (args[1].equalsIgnoreCase("reset")) {
			InvModeType.CATEGORY_COST_REMOVE.open((Player) sender);
			return true;
		}
		double cost;
		try {
			cost = Double.valueOf(args[1]);
		} catch (NumberFormatException e) {
			Lang.Command.Errors.number(args[1]).send(sender);
			return true;
		}
		if (cost < 0) {
			Lang.Command.Errors.negative(args[1]).send(sender);
			return true;
		}
		InvModeType.CATEGORY_COST.open((Player) sender).asType(CategoryCostMode.class).setCost(cost);
		return true;
	}

}
