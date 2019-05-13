package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.head.Head;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        String playerStr = args[1].toLowerCase();
        Player player = Bukkit.getPlayer(playerStr);
        String archive = args[2];
        int headId = Integer.parseInt(args[3]);

        if (player == null && !playerStr.equals("all")) {
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

            if (playerStr.equals("all")) {
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl == sender) continue;
                    pl.getInventory().addItem(item);
                    pl.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.receive", head.get().getName()));
                }
                sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.success", instance.getLocale().getMessage("general.word.everyone"), head.get().getName()));
            } else {
                player.getInventory().addItem(item);
                player.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.receive", head.get().getName()));
                sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.success", player.getName(), head.get().getName()));
            }

            return ReturnType.SUCCESS;
        } else {
            sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.give.notfound", head.get().getName()));
            return ReturnType.FAILURE;
        }
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
        return "/heads give <player/all> <global/local> <head_id>";
    }

    @Override
    public String getDescription() {
        return "Gives the specified player the specified amount of heads.";
    }
}
