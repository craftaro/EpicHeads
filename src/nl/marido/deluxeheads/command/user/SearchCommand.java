package nl.marido.deluxeheads.command.user;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.marido.deluxeheads.DeluxeHeads;
import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.oldmenu.mode.SearchMode;

public class SearchCommand extends AbstractCommand {

	@Override
	public String getCommandLabel(MainConfig config) {
		return config.getSearchCommand();
	}

	@Override
	public String getPermission() {
		return "deluxeheads.search";
	}

	@Override
	public Lang.HelpSection getHelp() {
		return Lang.Command.Search.help();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			Lang.Command.Errors.mustBePlayer().send(sender);
			return true;
		}

		if (args.length <= 1) {
			sendInvalidArgs(sender);
			return true;
		}

		StringBuilder queryBuilder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			queryBuilder.append(args[i]);
			queryBuilder.append(' ');
		}

		String query = queryBuilder.toString().trim();

		DeluxeHeads.getCache().searchHeadsAsync(query, matches -> {
			if (matches.size() == 0) {
				Lang.Command.Search.noneFound(query).send(sender);
				return;
			}

			Lang.Command.Search.found(query, matches.size()).send(sender);

			new SearchMode((Player) sender, matches);
		});

		return true;
	}
}
