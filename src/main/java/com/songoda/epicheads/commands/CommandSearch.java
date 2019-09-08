package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.gui.GUIHeads;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSearch extends AbstractCommand {

    final EpicHeads instance;

    public CommandSearch(EpicHeads instance) {
        super(true, "search");
        this.instance = instance;
    }

    @Override
    protected AbstractCommand.ReturnType runCommand(CommandSender sender, String... args) {
        GUIHeads.doSearch(instance, (Player) sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.search";
    }

    @Override
    public String getSyntax() {
        return "/heads search";
    }

    @Override
    public String getDescription() {
        return "Opens a gui displaying your search results.";
    }
}
