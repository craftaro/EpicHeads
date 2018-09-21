package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.oldmenu.mode.CategoryCostMode;
import com.songoda.epicheads.oldmenu.mode.InvModeType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCategoryCost extends AbstractCommand {

    public CommandCategoryCost(AbstractCommand parent) {
        super("remove", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 2) {
            return ReturnType.SYNTAX_ERROR;
        }
        if (args[1].equalsIgnoreCase("reset")) {
            InvModeType.CATEGORY_COST_REMOVE.open((Player) sender);
            return ReturnType.SUCCESS;
        }

        //ToDo: Gross...

        double cost;
        try {
            cost = Double.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(instance.getLocale().getMessage("command.error.number", args[1]));
            return ReturnType.FAILURE;
        }
        if (cost < 0) {
            sender.sendMessage(instance.getLocale().getMessage("command.error.negative", args[1]));
            return ReturnType.FAILURE;
        }
        InvModeType.CATEGORY_COST.open((Player) sender).asType(CategoryCostMode.class).setCost(cost);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.category-cost";
    }

    @Override
    public String getSyntax() {
        return "/heads categorycost <reset/new cost>";
    }

    @Override
    public String getDescription() {
        return "Set heads costs by category.";
    }
}