package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.head.Category;
import com.songoda.epicheads.head.Head;
import com.songoda.epicheads.head.HeadManager;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public class CommandAdd extends AbstractCommand {

    public CommandAdd(AbstractCommand parent) {
        super(parent, false, "add");
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length != 4) return ReturnType.SYNTAX_ERROR;

        String url = args[1];
        String name = args[2].replace("_", " ");
        String categoryStr = args[3].replace("_", " ");

        HeadManager headManager = instance.getHeadManager();

        if (headManager.getLocalHeads().stream().anyMatch(head -> head.getURL().equals(url))) {
            sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.add.exists"));
            return ReturnType.FAILURE;
        }

        List<Category> categories = headManager.getCategories().stream().filter(category1 -> category1.getName().equals(categoryStr)).collect(Collectors.toList());

        Category category = categories.isEmpty() ? new Category(categoryStr) : categories.get(0);

        headManager.addLocalHead(new Head(headManager.getNextLocalId(), name, url, category, null, (byte)0));

        sender.sendMessage(instance.getReferences().getPrefix() + instance.getLocale().getMessage("command.add.success", name));
        if (categories.isEmpty()) {
            instance.reload();
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(EpicHeads instance, CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "epicheads.add";
    }

    @Override
    public String getSyntax() {
        return "/heads add <url> <name> <category>";
    }

    @Override
    public String getDescription() {
        return "Adds a head to your local database. Including a category that does not exist will create new category. Make sure to use underscores and now spaces.";
    }
}
