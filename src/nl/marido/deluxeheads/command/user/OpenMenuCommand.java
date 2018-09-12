package nl.marido.deluxeheads.command.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.oldmenu.mode.InvModeType;

public class OpenMenuCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return null;
	}

	@Override
	public String getPermission() {
		return "deluxeheads.menu";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.OpenMenu.help();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Lang.Command.Errors.mustBePlayer().send(sender);
			return true;
		}

		InvModeType.GET.open((Player) sender);

		// CacheHeadsMenu.openHeadsMenu((Player) sender);

		return true;
	}

}
