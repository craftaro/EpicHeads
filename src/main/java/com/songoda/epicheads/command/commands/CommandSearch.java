package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.gui.GUIHeads;
import com.songoda.epicheads.head.Head;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandSearch extends AbstractCommand {

    public CommandSearch(AbstractCommand parent) {
        super(parent, true, "search");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {

        GUIHeads.doSearch((Player)sender);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.search";
    }

    @Override
    public String getSyntax() {
        return "/heads search";
    }

    @Override
    public String getDescription() {
        return "Opens a gui displaying your search results.";
    }
}
