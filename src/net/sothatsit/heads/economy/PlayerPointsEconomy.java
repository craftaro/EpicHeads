package net.sothatsit.heads.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPointsEconomy implements Economy {

    private PlayerPoints playerPoints;

    @Override
    public String getName() {
        return "PlayerPoints";
    }

    private int convertAmount(double amount) {
        return (int) Math.ceil(amount);
    }

    @Override
    public String formatBalance(double bal) {
        int amount = convertAmount(bal);

        return Integer.toString(amount);
    }

    @Override
    public boolean tryHook() {
        if (Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints") == null)
            return false;

        playerPoints = (PlayerPoints) Bukkit.getPluginManager().getPlugin("PlayerPoints");

        return true;
    }

    @Override
    public boolean isHooked() {
        return playerPoints != null;
    }

    @Override
    public boolean hasBalance(Player player, double bal) {
        int amount = convertAmount(bal);

        return playerPoints.getAPI().look(player.getUniqueId()) >= amount;
    }

    @Override
    public boolean takeBalance(Player player, double bal) {
        int amount = convertAmount(bal);

        return playerPoints.getAPI().take(player.getUniqueId(), amount);
    }

}
