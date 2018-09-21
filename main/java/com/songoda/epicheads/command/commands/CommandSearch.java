package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.oldmenu.mode.SearchMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSearch extends AbstractCommand {

    public CommandSearch(AbstractCommand parent) {
        super("search", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length <= 1) {
            return ReturnType.SYNTAX_ERROR;
        }

        StringBuilder queryBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            queryBuilder.append(args[i]);
            queryBuilder.append(' ');
        }

        String query = queryBuilder.toString().trim();

        EpicHeads.getInstance().getCache().searchHeadsAsync(query, matches -> {
            if (matches.size() == 0) {

               sender.sendMessage(instance.getLocale().getMessage("command.search.nonefound", query));
                return;
            }

            sender.sendMessage(instance.getLocale().getMessage("command.search.found", query, matches.size()));

            new SearchMode((Player) sender, matches);
        });

        return ReturnType.SUCCESS;
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.search";
    }

    @Override
    public String getSyntax() {
        return "/heads search <search query>";
    }

    @Override
    public String getDescription() {
        return "Find useful heads.";
    }
}
