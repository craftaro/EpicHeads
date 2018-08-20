package net.sothatsit.heads.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements Economy {

    private net.milkbowl.vault.economy.Economy economy;

    @Override
    public String getName() {
        return "Vault";
    }

    @Override
    public String formatBalance(double bal) {
        return Double.toString(bal);
    }

    @Override
    public boolean tryHook() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp =
                Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);

        if (rsp == null)
            return false;

        economy = rsp.getProvider();

        return economy != null;
    }

    @Override
    public boolean isHooked() {
        return economy != null;
    }

    @Override
    public boolean hasBalance(Player player, double bal) {
        return economy.has(player, bal);
    }

    @Override
    public boolean takeBalance(Player player, double bal) {
        return economy.withdrawPlayer(player, bal).transactionSuccess();
    }

}
