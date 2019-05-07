package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandGive extends AbstractCommand {

    public CommandGive(AbstractCommand parent) {
        super(parent, false, "give");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {

        if (args.length != 4) return ReturnType.SYNTAX_ERROR;

        Player player = Bukkit.getPlayer(args[1]);
        String archive = args[2];
        int headId = Integer.parseInt(args[3]);

        if (player == null) {
            sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.notonline", args[1]));
            return ReturnType.FAILURE;
        }

        List<Head> heads;

        if (archive.equalsIgnoreCase("global"))
            heads = instance.getHeadManager().getGlobalHeads();
        else if (archive.equalsIgnoreCase("local"))
            heads = instance.getHeadManager().getLocalHeads();
        else {
            return ReturnType.SYNTAX_ERROR;
        }

        Optional<Head> head = heads.stream().filter(h -> h.getId() == headId).findFirst();

        if (head.isPresent()) {
            ItemStack item = head.get().asItemStack();

            ItemMeta meta = item.getItemMeta();
            meta.setLore(new ArrayList<>());
            item.setItemMeta(meta);

            player.getInventory().addItem(item);
        } else {
            sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.notfound", head.get().getName()));
            return ReturnType.FAILURE;
        }

        sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.success", player.getName(), head.get().getName()));
        player.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.receive", head.get().getName()));

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.give";
    }

    @Override
    public String getSyntax() {
        return "/heads give <player> <global/local> <head_id>";
    }

    @Override
    public String getDescription() {
        return "Gives the specified player the specified amount of heads.";
    }
}
