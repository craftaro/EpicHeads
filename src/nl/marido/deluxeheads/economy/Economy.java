package nl.marido.deluxeheads.economy;

import org.bukkit.entity.Player;

public interface Economy {

    public String getName();

    public String formatBalance(double bal);

    public boolean tryHook();

    public boolean isHooked();

    public boolean hasBalance(Player player, double bal);

    public boolean takeBalance(Player player, double bal);

}
