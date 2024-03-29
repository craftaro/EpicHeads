package com.craftaro.epicheads.commands;

import com.craftaro.core.commands.AbstractCommand;
import com.craftaro.core.compatibility.CompatibleHand;
import com.craftaro.core.utils.ItemUtils;
import com.craftaro.epicheads.EpicHeads;
import com.craftaro.third_party.com.cryptomorin.xseries.SkullUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class CommandUrl extends AbstractCommand {
    private final EpicHeads plugin;

    public CommandUrl(EpicHeads plugin) {
        super(CommandType.PLAYER_ONLY, "url");
        this.plugin = plugin;
    }

    @Override
    protected AbstractCommand.ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        ItemStack item = CompatibleHand.MAIN_HAND.getItem(player);

        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof SkullMeta)) {
            return ReturnType.FAILURE;
        }

        String encodedStr = SkullUtils.getSkinValue(item.getItemMeta());
        if (encodedStr == null) {
            return ReturnType.FAILURE;
        }

        String url = ItemUtils.getDecodedTexture(encodedStr);

        player.sendMessage(this.plugin.getLocale().newMessage("http://textures.minecraft.net/texture/" + url).getPrefixedMessage());
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.url";
    }

    @Override
    public String getSyntax() {
        return "url";
    }

    @Override
    public String getDescription() {
        return "Gives you the texture url for the head you are holding.";
    }
}
