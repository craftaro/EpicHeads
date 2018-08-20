package net.sothatsit.heads.command.admin;

import net.sothatsit.heads.command.AbstractCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.mode.CostMode;
import net.sothatsit.heads.oldmenu.mode.InvModeType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CostCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getCostCommand();
    }

    @Override
    public String getPermission() {
        return "heads.cost";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Cost.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 2) {
            sendInvalidArgs(sender);
            return true;
        }
        
        double cost;
        try {
            cost = Double.valueOf(args[1]);
        } catch (NumberFormatException e) {
            Lang.Command.Errors.number(args[1]).send(sender);
            return true;
        }
        
        if (cost < 0) {
            Lang.Command.Errors.negative(args[1]).send(sender);
            return true;
        }
        
        InvModeType.COST.open((Player) sender).asType(CostMode.class).setCost(cost);
        return true;
    }
    
}
