package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.volatilecode.ItemNBT;
import com.songoda.epicheads.volatilecode.Items;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandId extends AbstractCommand {

    public CommandId(AbstractCommand parent) {
        super("id", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length != 1) {
            return ReturnType.SYNTAX_ERROR;
        }

        ItemStack hand = player.getInventory().getItemInHand();
        if (!Items.isSkull(hand)) {
            sender.sendMessage(instance.getLocale().getMessage("command.id.holdskull"));
            return ReturnType.FAILURE;
        }

        String texture = ItemNBT.getTextureProperty(hand);
        CacheHead head = EpicHeads.getCache().findHeadByTexture(texture);
        if (head == null) {
            ItemMeta meta = hand.getItemMeta();
            String name = ChatColor.stripColor(meta.hasDisplayName() ? meta.getDisplayName() : "");
            sender.sendMessage(instance.getLocale().getMessage("command.id.unknownhead", name));
            return ReturnType.FAILURE;
        }

        sender.sendMessage(instance.getLocale().getMessage("command.id.success", head.getName(), head.getId()));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.id";
    }

    @Override
    public String getSyntax() {
        return "/heads id";
    }

    @Override
    public String getDescription() {
        return "Get the ID for a player head.";
    }
}
