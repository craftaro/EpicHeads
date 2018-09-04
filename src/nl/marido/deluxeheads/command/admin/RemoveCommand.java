package nl.marido.deluxeheads.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.marido.deluxeheads.command.AbstractCommand;
import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;
import nl.marido.deluxeheads.oldmenu.mode.InvModeType;

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
