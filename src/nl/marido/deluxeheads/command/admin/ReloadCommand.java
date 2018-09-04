package nl.marido.deluxeheads.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;

public class ReloadCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getReloadCommand();
	}

	@Override
	public String getPermission() {
		return "heads.reload";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.Reload.help();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			sendInvalidArgs(sender);
			return true;
		}

		DeluxeHeads.getInstance().reloadConfigs();

		Lang.Command.Reload.reloaded().send(sender);
		return true;
	}
}
