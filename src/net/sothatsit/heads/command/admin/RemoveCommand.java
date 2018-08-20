package net.sothatsit.heads.command.admin;

import net.sothatsit.heads.command.AbstractCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.mode.InvModeType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getRemoveCommand();
    }

    @Override
    public String getPermission() {
        return "heads.remove";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Remove.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 1) {
            sendInvalidArgs(sender);
            return true;
        }
        
        InvModeType.REMOVE.open((Player) sender);
        return true;
    }
    
}
