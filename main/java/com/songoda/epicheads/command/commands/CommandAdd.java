package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.volatilecode.TextureGetter;
import org.bukkit.command.CommandSender;

public class CommandAdd extends AbstractCommand {

    public CommandAdd(AbstractCommand parent) {
        super("add", parent, false);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        //ToDO: Test to make sure this works.
        if (args.length < 3) {
            return ReturnType.SYNTAX_ERROR;
        }

        final String playerName = args[1];
        final String category = args[2];

        final String name;

        if (args.length > 3) {
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                nameBuilder.append(' ');
                nameBuilder.append(args[i]);
            }
            name = nameBuilder.toString().substring(1);
        } else {
            name = playerName;
        }

        if (category.length() > 32) {
            String[] parts = instance.getLocale().getMessage("command.add.categorylength", category, category.length()).split("\\|");
            for (String line : parts)
                sender.sendMessage(line);
            return ReturnType.FAILURE;
        }

        String texture = TextureGetter.getCachedTexture(playerName);

        if (texture != null) {
            add(instance, sender, category, name, playerName, texture);
        } else {
            sender.sendMessage(instance.getLocale().getMessage("command.add.fetching"));
            TextureGetter.getTexture(playerName, (resolvedTexture) ->
                    add(instance, sender, category, name, playerName, resolvedTexture));
        }
        return ReturnType.SUCCESS;
    }

    public void add(EpicHeads instance, CommandSender sender, String category, String name, String playerName, String texture) {
        if (texture == null || texture.isEmpty()) {
            String[] parts = instance.getLocale().getMessage("command.add.cantfind", playerName).split("\\|");
            for (String line : parts)
                sender.sendMessage(line);
            return;
        }

        CacheHead head = new CacheHead(name, category, texture);

        instance.getCache().addHead(head);
        instance.saveCache();

        sender.sendMessage(instance.getLocale().getMessage("command.add.added", name, category));
    }


    @Override
    public String getPermissionNode() {
        return "epicheads.add";
    }

    @Override
    public String getSyntax() {
        return "/heads add <player-name> <category> [head name]";
    }

    @Override
    public String getDescription() {
        return "Add a new head to the menu.";
    }
}
