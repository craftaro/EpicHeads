package net.sothatsit.heads.command.admin;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.command.AbstractCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
