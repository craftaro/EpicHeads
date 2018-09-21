package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.oldmenu.mode.InvModeType;
import com.songoda.epicheads.oldmenu.mode.RenameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRename extends AbstractCommand {

    public CommandRename(AbstractCommand parent) {
        super("rename", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length <= 1) {
            return ReturnType.SYNTAX_ERROR;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            if (i != 1) {
                builder.append(' ');
            }

            builder.append(args[i]);
        }

        String name = builder.toString();

        InvModeType.RENAME.open((Player) sender).asType(RenameMode.class).setName(name);
        return ReturnType.SUCCESS;
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.rename";
    }

    @Override
    public String getSyntax() {
        return "/heads rename <new name>";
    }

    @Override
    public String getDescription() {
        return "Rename a head in the menu.";
    }
}
