package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.menu.ui.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import sun.management.Sensor;

public class CommandItemEco extends AbstractCommand {

    public CommandItemEco(AbstractCommand parent) {
        super("itemeco", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length < 2) {
            return ReturnType.SYNTAX_ERROR;
        }

        if (args[1].equalsIgnoreCase("give")) {
            return onGiveCommand(instance, sender, args);
        }

        Player player = (Player) sender; //ToDo: This is wrong.

        if (args[1].equalsIgnoreCase("set")) {
            return onSetCommand(instance, player, args);
        }

        if (args[1].equalsIgnoreCase("get")) {
            return onGetCommand(instance, player, args);
        }

        return ReturnType.SYNTAX_ERROR;
    }

    private ReturnType onSetCommand(EpicHeads instance, Player player, String[] args) {
        if (args.length != 2) {
            return ReturnType.SYNTAX_ERROR;
        }

        @SuppressWarnings("deprecation")
        // Had to do this to resolve the compatibility issue with 1.13.
                ItemStack itemStack = player.getInventory().getItemInHand();

        if (itemStack == null) {
            player.sendMessage(instance.getLocale().getMessage("command.itemeco.noitem"));
            return ReturnType.FAILURE;
        }

        Item item = Item.create(itemStack).amount(1);

        EpicHeads.getInstance().getMainConfig().setItemEcoItem(item);

        player.sendMessage(instance.getLocale().getMessage("command.itemeco.set"));
        return ReturnType.SUCCESS;
    }

    private ReturnType onGetCommand(EpicHeads instance, Player player, String[] args) {
        if (args.length != 2 && args.length != 3) {
            return ReturnType.SYNTAX_ERROR;
        }

        int amount = 1;

        if (args.length == 3) {
            try {
                amount = Integer.valueOf(args[2]);
            } catch (NumberFormatException e) {
                player.sendMessage(instance.getLocale().getMessage("command.error.integer", args[2]));
                return ReturnType.FAILURE;
            }

            if (amount < 1) {
                player.sendMessage(instance.getLocale().getMessage("command.error.negative", args[2]));
                return ReturnType.FAILURE;
            }
        }

        giveTokens(player, amount);

        player.sendMessage(instance.getLocale().getMessage("command.itemeco.get", amount));
        return ReturnType.SUCCESS;
    }

    private ReturnType onGiveCommand(EpicHeads instance, CommandSender sender, String[] args) {
        if (args.length != 3 && args.length != 4) {
            return ReturnType.SYNTAX_ERROR;
        }

        int amount = 1;

        if (args.length == 4) {
            try {
                amount = Integer.valueOf(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage(instance.getLocale().getMessage("command.error.integer", args[3]));
                return ReturnType.FAILURE;
            }

            if (amount < 1) {
                sender.sendMessage(instance.getLocale().getMessage("command.error.negative", args[3]));
                return ReturnType.FAILURE;
            }
        }

        Player player = Bukkit.getPlayer(args[2]);

        if (player == null) {
            sender.sendMessage(instance.getLocale().getMessage("command.give.cantfindplayer", args[2]));
            return ReturnType.FAILURE;
        }

        giveTokens(player, amount);

        player.sendMessage(instance.getLocale().getMessage("command.itemeco.get", amount));
        sender.sendMessage(instance.getLocale().getMessage("command.itemeco.given", amount).replace("%player%",player.getDisplayName()));
        return ReturnType.SUCCESS;
    }

    private void giveTokens(Player player, int amount) {
        while (amount > 0) {
            int giveAmount = Math.min(64, amount);
            amount -= giveAmount;

            ItemStack itemStack = EpicHeads.getInstance().getMainConfig().getItemEconomyItem().amount(giveAmount).build();

            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(itemStack);
            } else {
                org.bukkit.entity.Item item = player.getWorld().dropItemNaturally(player.getEyeLocation(), itemStack);

                item.setPickupDelay(0);
            }
        }
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.item-eco";
    }

    @Override
    public String getSyntax() {
        return "/heads itemeco <get/set/give>";
    }

    @Override
    public String getDescription() {
        return "Manage the item economy.";
    }
}
