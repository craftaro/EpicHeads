package net.sothatsit.heads.economy;

import org.bukkit.entity.Player;

public class NoEconomy implements Economy {

    @Override
    public String getName() {
        return "No";
    }

    @Override
    public String formatBalance(double bal) {
        return Double.toString(bal);
    }

    @Override
    public boolean tryHook() {
        return true;
    }

    @Override
    public boolean isHooked() {
        return true;
    }

    @Override
    public boolean hasBalance(Player player, double bal) {
        return true;
    }

    @Override
    public boolean takeBalance(Player player, double bal) {
        return false;
    }

}
