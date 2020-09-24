package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.ItemUtils;
import com.songoda.epicheads.EpicHeads;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class CommandBase64 extends AbstractCommand {

    private final EpicHeads plugin;

    public CommandBase64(EpicHeads plugin) {
        super(CommandType.PLAYER_ONLY, "base64");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {

        Player player = (Player) sender;

        ItemStack item = player.getItemInHand();

        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof SkullMeta)) return ReturnType.FAILURE;

        String encodededStr = ItemUtils.getSkullTexture(item);

        if (encodededStr == null) return ReturnType.FAILURE;

        plugin.getLocale().newMessage(encodededStr).sendPrefixedMessage(player);

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
        return "base64";
    }

    @Override
    public String getDescription() {
        return "Gives you the base64 code of the head you are holding.";
    }

}
