package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class CommandRandom extends AbstractCommand {

    private static final Random RANDOM = new Random();

    public CommandRandom(AbstractCommand parent) {
        super("random", parent, false);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 1 && args.length != 2) {
            return ReturnType.SYNTAX_ERROR;
        }

        if (EpicHeads.getCache().getHeadCount() == 0) {
            sender.sendMessage(instance.getLocale().getMessage("command.random.noheads"));
            return ReturnType.FAILURE;
        }

        CacheHead random = EpicHeads.getCache().getRandomHead(RANDOM);

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return ReturnType.NO_CONSOLE;
            }

            sender.sendMessage(instance.getLocale().getMessage("command.random.self", random));

            ((Player) sender).getInventory().addItem(random.getItemStack());
            return ReturnType.SUCCESS;
        }

        Player player = Bukkit.getPlayer(args[1]);

        if (player == null) {
            sender.sendMessage(instance.getLocale().getMessage("command.give.cantfindplayer", args[1]));
            return ReturnType.FAILURE;
        }

        player.sendMessage(instance.getLocale().getMessage("command.random.give", random));
        sender.sendMessage(instance.getLocale().getMessage("command.give.success", 1, random, player));

        player.getInventory().addItem(random.getItemStack());
        return ReturnType.SUCCESS;
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.random";
    }

    @Override
    public String getSyntax() {
        return "/heads random [player]";
    }

    @Override
    public String getDescription() {
        return "RGet or give a random head.";
    }
}
