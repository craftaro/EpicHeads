package nl.marido.deluxeheads.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import nl.marido.deluxeheads.config.MainConfig;
import nl.marido.deluxeheads.config.lang.Lang;

public abstract class AbstractCommand implements CommandExecutor {

    public abstract String getCommandLabel(MainConfig config);

    public abstract String getPermission();

    public abstract Lang.HelpSection getHelp();

    public void sendInvalidArgs(CommandSender sender) {
        getHelp().sendInvalidArgs(sender);
    }
    
}
