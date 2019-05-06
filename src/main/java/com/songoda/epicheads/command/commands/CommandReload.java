package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandReload extends AbstractCommand {

    public CommandReload(AbstractCommand parent) {
        super(parent, false, "reload");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        instance.reload();
        sender.sendMessage(Methods.formatText(instance.getReferences().getPrefix() + "&7Configuration and Language files reloaded."));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.admin";
    }

    @Override
    public String getSyntax() {
        return "/epicheads reload";
    }

    @Override
    public String getDescription() {
        return "Reload the Configuration and Language files.";
    }
}
