package com.songoda.epicheads.utils;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.hooks.economies.Economy;
import com.songoda.core.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ItemEconomy extends Economy {

    public boolean isItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR)
            return false;
        if (CompatibleMaterial.getMaterial(itemStack) == CompatibleMaterial.PLAYER_HEAD)
            return ItemUtils.getSkullTexture(itemStack).equals(ItemUtils.getSkullTexture(Methods.createToken(1)));
        return itemStack.isSimilar(Methods.createToken(1));
    }

    private int convertAmount(double amount) {
        return (int) Math.ceil(amount);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        int amount = 0;
        for (ItemStack item : player.getPlayer().getInventory().getContents()) {
            if (!isItem(item))
                continue;
            amount += item.getAmount();
        }
        return amount;
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

    @Override
    public String getName() {
        return "Item";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
