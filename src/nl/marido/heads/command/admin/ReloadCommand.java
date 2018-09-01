package nl.marido.heads.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import nl.marido.heads.Heads;
import nl.marido.heads.command.AbstractCommand;
import nl.marido.heads.config.MainConfig;
import nl.marido.heads.config.lang.Lang;

public class ReloadCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getReloadCommand();
    }

    @Override
    public String getPermission() {
        return "heads.reload";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Reload.help();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1) {
            sendInvalidArgs(sender);
            return true;
        }

        Heads.getInstance().reloadConfigs();

        Lang.Command.Reload.reloaded().send(sender);
        return true;
    }
}
