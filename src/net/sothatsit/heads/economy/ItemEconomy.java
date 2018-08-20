package net.sothatsit.heads.economy;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.menu.ui.item.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemEconomy implements Economy {

    public boolean isItem(ItemStack itemStack) {
        if(itemStack == null)
            return false;

        Item item = Item.create(itemStack).amount(1);

        return item.equals(Heads.getMainConfig().getItemEconomyItem());
    }

    @Override
    public String getName() {
        return "Item";
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
        return true;
    }

    @Override
    public boolean isHooked() {
        return true;
    }

    @Override
    public boolean hasBalance(Player player, double bal) {
        int amount = convertAmount(bal);

        for(ItemStack item : player.getInventory().getContents()) {
            if(!isItem(item))
                continue;

            if(amount <= item.getAmount())
                return true;

            amount -= item.getAmount();
        }

        return false;
    }

    @Override
    public boolean takeBalance(Player player, double bal) {
        int amount = convertAmount(bal);

        ItemStack[] contents = player.getInventory().getContents();
        for(int index = 0; index < contents.length; ++index) {
            ItemStack item = contents[index];

            if(!isItem(item))
                continue;

            if(amount >= item.getAmount()) {
                amount -= item.getAmount();
                contents[index] = null;
            } else {
                item.setAmount(item.getAmount() - amount);
                amount = 0;
            }

            if(amount == 0)
                break;
        }

        if(amount != 0)
            return false;

        player.getInventory().setContents(contents);

        return true;
    }

}
