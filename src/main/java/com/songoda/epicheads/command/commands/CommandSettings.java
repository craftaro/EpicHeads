package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSettings extends AbstractCommand {

    public CommandSettings(AbstractCommand parent) {
        super(parent, true, "settings");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        Player player = (Player) sender;
        instance.getSettingsManager().openSettingsManager(player);
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
        return "/ehe settings";
    }

    @Override
    public String getDescription() {
        return "Edit EpicHeads Settings.";
    }
}
