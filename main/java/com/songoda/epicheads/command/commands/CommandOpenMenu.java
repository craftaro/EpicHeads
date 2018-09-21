package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.oldmenu.mode.InvModeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandOpenMenu extends AbstractCommand {

    public CommandOpenMenu() {
        super("EpicHeads", null, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 0) return ReturnType.SYNTAX_ERROR;

        InvModeType.GET.open((Player) sender);
        return ReturnType.SUCCESS;
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
        return "Open the heads menu.";
    }
}
