package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class CommandUrl extends AbstractCommand {

    public CommandUrl(AbstractCommand parent) {
        super(parent, true, "url");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {

        Player player = (Player)sender;

        ItemStack item = player.getItemInHand();

        if (!item.hasItemMeta() || !(item.getItemMeta() instanceof SkullMeta)) return ReturnType.FAILURE;

        String encodededStr = Methods.getEncodedTexture(item);
        String url = Methods.getDecodedTexture(encodededStr);

        player.sendMessage(instance.getReferences().getPrefix());
        player.sendMessage("http://textures.minecraft.net/texture/" + url);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.url";
    }

    @Override
    public String getSyntax() {
        return "/heads url";
    }

    @Override
    public String getDescription() {
        return "Gives you the texture url for the head you are holding.";
    }
}
