package nl.marido.heads.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.marido.heads.command.AbstractCommand;
import nl.marido.heads.config.MainConfig;
import nl.marido.heads.config.lang.Lang;
import nl.marido.heads.oldmenu.mode.InvModeType;
import nl.marido.heads.oldmenu.mode.RenameMode;

public class RenameCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getRenameCommand();
    }

    @Override
    public String getPermission() {
        return "heads.rename";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Rename.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length <= 1) {
            sendInvalidArgs(sender);
            return true;
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
        return true;
    }
    
}
