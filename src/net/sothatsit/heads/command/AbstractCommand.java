package net.sothatsit.heads.command;

import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AbstractCommand implements CommandExecutor {

    public abstract String getCommandLabel(MainConfig config);

    public abstract String getPermission();

    public abstract Lang.HelpSection getHelp();

    public void sendInvalidArgs(CommandSender sender) {
        getHelp().sendInvalidArgs(sender);
    }
    
}
