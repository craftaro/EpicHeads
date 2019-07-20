package com.songoda.epicheads.economy;

import com.songoda.epicheads.utils.Methods;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ItemEconomy implements Economy {


    public boolean isItem(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        return itemStack.isSimilar(Methods.createToken(1));
    }


    private int convertAmount(double amount) {
        return (int) Math.ceil(amount);
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double cost) {
        int amount = convertAmount(cost);
        for (ItemStack item : player.getPlayer().getInventory().getContents()) {
            if (!isItem(item))
                continue;
            if (amount <= item.getAmount())
                return true;
            amount -= item.getAmount();
        }
        return false;
    }


    @Override
    public boolean withdrawBalance(OfflinePlayer player, double cost) {
        int amount = convertAmount(cost);
        ItemStack[] contents = player.getPlayer().getInventory().getContents();
        for (int index = 0; index < contents.length; ++index) {
            ItemStack item = contents[index];
            if (!isItem(item))
                continue;
            if (amount >= item.getAmount()) {
                amount -= item.getAmount();
                contents[index] = null;
            } else {
                item.setAmount(item.getAmount() - amount);
                amount = 0;
            }
            if (amount == 0)
                break;
        }
        if (amount != 0)
            return false;
        player.getPlayer().getInventory().setContents(contents);

        return true;

    }

    @Override
    public boolean deposit(OfflinePlayer player, double amount) {
        return false;
    }
}
