package nl.marido.deluxeheads.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.command.admin.AddCommand;
import nl.marido.deluxeheads.command.admin.CategoryCostCommand;
import nl.marido.deluxeheads.command.admin.CostCommand;
import nl.marido.deluxeheads.command.admin.GiveCommand;
import nl.marido.deluxeheads.command.admin.HandCommand;
import nl.marido.deluxeheads.command.admin.IdCommand;
import nl.marido.deluxeheads.command.admin.ItemEcoCommand;
import nl.marido.deluxeheads.command.admin.ReloadCommand;
import nl.marido.deluxeheads.command.admin.RemoveCommand;
import nl.marido.deluxeheads.command.admin.RenameCommand;
import nl.marido.deluxeheads.command.user.GetCommand;
import nl.marido.deluxeheads.command.user.OpenMenuCommand;
import nl.marido.deluxeheads.command.user.RandomCommand;
import nl.marido.deluxeheads.command.user.SearchCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.config.lang.LangMessage;

public class HeadsCommand implements CommandExecutor {

	private static final OpenMenuCommand openMenu = new OpenMenuCommand();
	private static final HelpCommand help = new HelpCommand();

	public static final AbstractCommand[] commands = { new OpenMenuCommand(), new SearchCommand(), new GetCommand(), new RandomCommand(),

			new AddCommand(), new HandCommand(), new RemoveCommand(), new RenameCommand(), new IdCommand(), new GiveCommand(), new CostCommand(), new CategoryCostCommand(), new ItemEcoCommand(),

			new ReloadCommand(), new HelpCommand() };

	@Override
	public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] args) {
		if (args.length == 0) {
			String permission = openMenu.getPermission();

			if (permission != null && !sender.hasPermission(permission)) {
				Lang.Command.Errors.noPermission().send(sender);
				return true;
			}

			return openMenu.onCommand(sender, bukkitCommand, label, args);
		}

		String argument = args[0];
		MainConfig config = DeluxeHeads.getMainConfig();

		for (AbstractCommand command : commands) {
			String commandLabel = command.getCommandLabel(config);

			if (commandLabel == null || !argument.equalsIgnoreCase(commandLabel))
				continue;

			String permission = command.getPermission();

			if (permission != null && !sender.hasPermission(permission)) {
				Lang.Command.Errors.noPermission().send(sender);
				return true;
			}

			return command.onCommand(sender, bukkitCommand, label, args);
		}

		LangMessage unknownCommandMessage = Lang.Command.unknownCommand(argument);

		unknownCommandMessage.send(sender);

		return help.onCommand(sender, new String[0], 10 - unknownCommandMessage.getLineCount());
	}

}
