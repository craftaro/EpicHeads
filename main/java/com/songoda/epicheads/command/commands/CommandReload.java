package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public class CommandReload extends AbstractCommand {

    public CommandReload(AbstractCommand parent) {
        super("reload", parent, false);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 1) return ReturnType.SYNTAX_ERROR;

        EpicHeads.getInstance().reloadConfigs();

        sender.sendMessage(instance.getLocale().getMessage("command.reload.success"));
        return ReturnType.SUCCESS;
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.reload";
    }

    @Override
    public String getSyntax() {
        return "/heads reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Heads config files.";
    }
}
