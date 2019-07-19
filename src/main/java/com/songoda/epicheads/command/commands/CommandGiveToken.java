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
            instance.getLocale().getMessage("command.give.notonline")
                    .processPlaceholder("name", args[1]).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        player.getInventory().addItem(Methods.createToken(amount));

        instance.getLocale().getMessage("command.givetoken.receive")
                .processPlaceholder("amount", amount).sendPrefixedMessage(player);

        if (player != sender)
            instance.getLocale().getMessage("command.givetoken.success")
                    .processPlaceholder("player", player.getName())
                    .processPlaceholder("amount", amount).sendPrefixedMessage(sender);

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
