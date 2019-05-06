package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.gui.GUIOverview;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandEpicHeads extends AbstractCommand {

    public CommandEpicHeads() {
        super(true, false, "EpicHeads");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        new GUIOverview(instance, (Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.menu";
    }

    @Override
    public String getSyntax() {
        return "/epicheads";
    }

    @Override
    public String getDescription() {
        return "Displays heads overview.";
    }
}
