package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.oldmenu.mode.InvModeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemove extends AbstractCommand {

    public CommandRemove(AbstractCommand parent) {
        super("remove", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 1) {
            return ReturnType.SYNTAX_ERROR;
        }

        //ToDo: Should be some kind of success message.
        InvModeType.REMOVE.open((Player) sender);
        return ReturnType.SUCCESS;
    }
    @Override
    public String getPermissionNode() {
        return "epicheads.remove";
    }

    @Override
    public String getSyntax() {
        return "/heads remove";
    }

    @Override
    public String getDescription() {
        return "Remove a head in the menu.";
    }
}