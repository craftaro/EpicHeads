package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.volatilecode.TextureGetter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandGet extends AbstractCommand {

    public CommandGet(AbstractCommand parent) {
        super("get", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 2) {
            return ReturnType.SYNTAX_ERROR;
        }

        String texture = TextureGetter.getCachedTexture(args[1]);

        if (texture != null) {
            giveHead(instance, (Player) sender, args[1], texture);
            return ReturnType.SUCCESS;
        }

        sender.sendMessage(instance.getLocale().getMessage("command.add.fetching"));

        final UUID uuid = ((Player) sender).getUniqueId();
        final String name = args[1];

        TextureGetter.getTexture(name, (resolvedTexture) -> {
            giveHead(instance, Bukkit.getPlayer(uuid), name, resolvedTexture);
        });
        return ReturnType.SUCCESS;
    }

    private void giveHead(EpicHeads instance, Player player, String name, String texture) {
        if (player != null) {
            if (texture == null || texture.isEmpty()) {
                player.sendMessage(instance.getLocale().getMessage("command.give.cantfindhead", name));
                return;
            }

            CacheHead head = new CacheHead(name, "getcommand", texture);

            player.sendMessage(instance.getLocale().getMessage("command.get.success", name));
            player.getInventory().addItem(head.getItemStack());
            player.updateInventory();
        }
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.get";
    }

    @Override
    public String getSyntax() {
        return "/heads get <player name>";
    }

    @Override
    public String getDescription() {
        return "Get a players head.";
    }
}
