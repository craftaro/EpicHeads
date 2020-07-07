package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicheads.EpicHeads;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {

    final EpicHeads instance;

    public CommandReload(EpicHeads instance) {
        super(false, "reload");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        instance.reloadConfig();
        instance.getLocale().getMessage("&7Configuration and Language files reloaded.").sendPrefixedMessage(sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.admin";
    }

    @Override
    public String getSyntax() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }
}
