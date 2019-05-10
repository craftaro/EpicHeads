package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandGiveToken extends AbstractCommand {

    public CommandGiveToken(AbstractCommand parent) {
        super(parent, false, "givetoken");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {

        Player player = Bukkit.getPlayer(args[1]);
        int amount = Integer.valueOf(args[2]);

        if (player == null) {
            sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.notonline", args[1]));
            return ReturnType.FAILURE;
        }

        player.getInventory().addItem(Methods.createToken(amount));

        player.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.givetoken.receive", amount));

        if (player != sender)
            sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.givetoken.success", player.getName(), amount));

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.givetoken";
    }

    @Override
    public String getSyntax() {
        return "/heads givetoken <player> <amount>";
    }

    @Override
    public String getDescription() {
        return "Gives the player a specified amount of player head tokens.";
    }
}
