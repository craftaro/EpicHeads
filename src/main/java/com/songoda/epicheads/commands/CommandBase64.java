package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class CommandBase64 extends AbstractCommand {

    final EpicHeads instance;

    public CommandBase64(EpicHeads instance) {
        super(true, "base64");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {

        Player player = (Player) sender;

        ItemStack item = player.getItemInHand();

        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof SkullMeta)) return ReturnType.FAILURE;

        String encodededStr = Methods.getEncodedTexture(item);

        if (encodededStr == null) return ReturnType.FAILURE;

        instance.getLocale().newMessage(encodededStr).sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
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
