package net.sothatsit.heads.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.command.admin.AddCommand;
import net.sothatsit.heads.command.admin.CategoryCostCommand;
import net.sothatsit.heads.command.admin.CostCommand;
import net.sothatsit.heads.command.admin.GiveCommand;
import net.sothatsit.heads.command.admin.HandCommand;
import net.sothatsit.heads.command.admin.IdCommand;
import net.sothatsit.heads.command.admin.ItemEcoCommand;
import net.sothatsit.heads.command.admin.ReloadCommand;
import net.sothatsit.heads.command.admin.RemoveCommand;
import net.sothatsit.heads.command.admin.RenameCommand;
import net.sothatsit.heads.command.user.GetCommand;
import net.sothatsit.heads.command.user.OpenMenuCommand;
import net.sothatsit.heads.command.user.RandomCommand;
import net.sothatsit.heads.command.user.SearchCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.config.lang.LangMessage;

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
		MainConfig config = Heads.getMainConfig();

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
