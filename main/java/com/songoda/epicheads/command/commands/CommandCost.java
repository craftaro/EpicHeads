package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.oldmenu.mode.CostMode;
import com.songoda.epicheads.oldmenu.mode.InvModeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCost extends AbstractCommand {

    public CommandCost(AbstractCommand parent) {
        super("cost", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 2) {
            return ReturnType.SYNTAX_ERROR;
        }

        double cost;
        try { //ToDo: This is so gross.
            cost = Double.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(instance.getLocale().getMessage("command.error.number", args[1]));
            return ReturnType.FAILURE;
        }

        if (cost < 0) {
            sender.sendMessage(instance.getLocale().getMessage("command.error.negative", args[1]));
            return ReturnType.FAILURE;
        }

        InvModeType.COST.open((Player) sender).asType(CostMode.class).setCost(cost);
        //ToDo: Should probably be some form of success message.
        return ReturnType.SUCCESS;
    }
    @Override
    public String getPermissionNode() {
        return "epicheads.id";
    }

    @Override
    public String getSyntax() {
        return "/heads cost <new cost>";
    }

    @Override
    public String getDescription() {
        return "Set a heads cost in the menu.";
    }
}
