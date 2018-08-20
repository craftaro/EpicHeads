package net.sothatsit.heads.command.user;

import net.sothatsit.heads.command.AbstractCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;

import net.sothatsit.heads.oldmenu.mode.InvModeType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenMenuCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return null;
    }

    @Override
    public String getPermission() {
        return "heads.menu";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.OpenMenu.help();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        InvModeType.GET.open((Player) sender);

        // CacheHeadsMenu.openHeadsMenu((Player) sender);

        return true;
    }
    
}
