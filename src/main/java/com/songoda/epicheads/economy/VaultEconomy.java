package com.songoda.epicheads.economy;

import com.songoda.epicheads.EpicHeads;
import org.bukkit.entity.Player;

public class VaultEconomy implements Economy {

    private final EpicHeads plugin;

    private final net.milkbowl.vault.economy.Economy vault;

    public VaultEconomy(EpicHeads plugin) {
        this.plugin = plugin;

        this.vault = plugin.getServer().getServicesManager().
                getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
    }

    @Override
    public boolean hasBalance(Player player, double cost) {
        return vault.has(player, cost);
    }

    @Override
    public boolean withdrawBalance(Player player, double cost) {
        return vault.withdrawPlayer(player, cost).transactionSuccess();
    }
}
