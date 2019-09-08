package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.gui.GUIOverview;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandEpicHeads extends AbstractCommand {

    final EpicHeads instance;

    public CommandEpicHeads(EpicHeads instance) {
        super(false, "EpicHeads");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        instance.getGuiManager().showGUI((Player) sender, new GUIOverview(instance, (Player) sender));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.menu";
    }

    @Override
    public String getSyntax() {
        return "/heads";
    }

    @Override
    public String getDescription() {
        return "Displays heads overview.";
    }
}
