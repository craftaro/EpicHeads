package com.songoda.epicheads.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.epicheads.EpicHeads;
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

    final EpicHeads instance;

    public CommandGive(EpicHeads instance) {
        super(false, "give");
        this.instance = instance;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length != 3) return ReturnType.SYNTAX_ERROR;

        String playerStr = args[0].toLowerCase();
        Player player = Bukkit.getPlayer(playerStr);
        String archive = args[1];
        int headId = Integer.parseInt(args[2]);

        if (player == null && !playerStr.equals("all")) {
            instance.getLocale().getMessage("command.give.notonline")
                    .processPlaceholder("name", args[1]).sendPrefixedMessage(sender);
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

                    instance.getLocale().getMessage("command.give.receive")
                            .processPlaceholder("name", head.get().getName()).sendPrefixedMessage(pl);
                }
                instance.getLocale().getMessage("command.give.success")
                        .processPlaceholder("player", instance.getLocale().getMessage("general.word.everyone").getMessage())
                        .processPlaceholder("name", head.get().getName())
                        .sendPrefixedMessage(sender);
            } else {
                player.getInventory().addItem(item);
                instance.getLocale().getMessage("command.give.receive")
                        .processPlaceholder("name", head.get().getName()).sendPrefixedMessage(player);
                instance.getLocale().getMessage("command.give.success")
                        .processPlaceholder("player", player.getName())
                        .processPlaceholder("name", head.get().getName())
                        .sendPrefixedMessage(sender);
            }

            return ReturnType.SUCCESS;
        } else {
            instance.getLocale().getMessage("command.give.notfound")
                    .processPlaceholder("name", head.get().getName()).sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
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
