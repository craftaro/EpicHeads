package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGive extends AbstractCommand {

    public CommandGive(AbstractCommand parent) {
        super("give", parent, false);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 4) {
            return ReturnType.SYNTAX_ERROR;
        }

        //ToDO: This is gross.

        int id;
        try {
            id = Integer.valueOf(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(instance.getLocale().getMessage("command.error.integer", args[1]));
            return ReturnType.FAILURE;
        }

        int amount;
        try {
            amount = Integer.valueOf(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(instance.getLocale().getMessage("command.give.invalidamount", args[3]));
            return ReturnType.FAILURE;
        }

        if (amount <= 0) {
            sender.sendMessage(instance.getLocale().getMessage("command.give.invalidamount", args[3]));
        }

        Player player = Bukkit.getPlayer(args[2]);

        if (player == null || !player.isOnline()) {
            sender.sendMessage(instance.getLocale().getMessage("command.give.cantfindplayer", args[2]));
            return ReturnType.FAILURE;
        }

        CacheHead head = EpicHeads.getInstance().getCache().findHead(id);

        if (head == null) {
            sender.sendMessage(instance.getLocale().getMessage("command.give.cantfindhead", id));
            return ReturnType.FAILURE;
        }

        ItemStack headItem = head.getItemStack();
        for (int i = 0; i < amount; i++) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(headItem.clone());
            } else {
                Item item = player.getWorld().dropItemNaturally(player.getEyeLocation(), headItem.clone());
                item.setPickupDelay(0);
            }
        }

        sender.sendMessage(instance.getLocale().getMessage("command.give.success", amount, head.getName(), player.getName()));
        return ReturnType.SUCCESS;
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.give";
    }

    @Override
    public String getSyntax() {
        return "/heads itemeco give <player> [amount]";
    }

    @Override
    public String getDescription() {
        return "Give the economy item to a player.";
    }
}