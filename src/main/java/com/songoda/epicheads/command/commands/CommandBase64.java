package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class CommandBase64 extends AbstractCommand {

    public CommandBase64(AbstractCommand parent) {
        super(parent, true, "base64");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {

        Player player = (Player)sender;

        ItemStack item = player.getItemInHand();

        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof SkullMeta)) return ReturnType.FAILURE;

        String encodededStr = Methods.getEncodedTexture(item);

        if (encodededStr == null) return ReturnType.FAILURE;

        player.sendMessage(instance.getReferences().getPrefix());
        player.sendMessage(encodededStr);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.base64";
    }

    @Override
    public String getSyntax() {
        return "/heads base64";
    }

    @Override
    public String getDescription() {
        return "Gives you the base64 code of the head you are holding.";
    }
}
