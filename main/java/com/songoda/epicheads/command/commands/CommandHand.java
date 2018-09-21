package com.songoda.epicheads.command.commands;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.command.AbstractCommand;
import com.songoda.epicheads.volatilecode.ItemNBT;
import com.songoda.epicheads.volatilecode.Items;
import com.songoda.epicheads.volatilecode.TextureGetter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CommandHand extends AbstractCommand {

    public CommandHand(AbstractCommand parent) {
        super("hand", parent, true);
    }

    @Override
    protected ReturnType runCommand(EpicHeads instance, CommandSender sender, String... args) {
        if (args.length < 3) {
            return ReturnType.SYNTAX_ERROR;
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            nameBuilder.append(' ');
            nameBuilder.append(args[i]);
        }

        String name = nameBuilder.toString().substring(1);
        String category = args[1];

        if (category.length() > 32) {
            String[] parts = instance.getLocale().getMessage("command.add.categorylength", category, category.length()).split("\\|");
            for (String line : parts)
                sender.sendMessage(line);
            return ReturnType.FAILURE;
        }

        Player player = (Player) sender;

        ItemStack hand = player.getInventory().getItemInHand();

        if (!Items.isSkull(hand)) {
            sender.sendMessage(instance.getLocale().getMessage("&cYou need to have a player skull in your hand to get its texture"));
            return ReturnType.FAILURE;
        }

        String texture = ItemNBT.getTextureProperty(hand);

        if (texture == null || texture.isEmpty()) {
            sender.sendMessage(instance.getLocale().getMessage("command.hand.notextureproperty"));

            SkullMeta meta = (SkullMeta) hand.getItemMeta();

            @SuppressWarnings("deprecation")
            final String owner = meta.getOwner();

            if (owner == null || owner.isEmpty()) {
                sender.sendMessage(instance.getLocale().getMessage("command.hand.nonameproperty"));
                return ReturnType.FAILURE;
            }

            texture = TextureGetter.getCachedTexture(owner);

            if (texture == null || texture.isEmpty()) {
                sender.sendMessage(instance.getLocale().getMessage("command.add.fetching"));
                TextureGetter.getTexture(owner, (resolvedTexture) -> {
                    if (resolvedTexture == null || resolvedTexture.isEmpty()) {
                        sender.sendMessage(instance.getLocale().getMessage("command.add.cantfind"));
                        return;
                    }

                    add(instance, sender, category, name, resolvedTexture);
                });
                return ReturnType.SUCCESS;
            }
        }

        add(instance, sender, category, name, texture);
        return ReturnType.SUCCESS;
    }

    public void add(EpicHeads instance, CommandSender sender, String category, String name, String texture) {
        CacheHead head = new CacheHead(name, category, texture);

        EpicHeads.getCache().addHead(head);
        EpicHeads.getInstance().saveCache();

        sender.sendMessage(instance.getLocale().getMessage("command.add.added", name, category));
    }



    @Override
    public String getPermissionNode() {
        return "epicheads.hand";
    }

    @Override
    public String getSyntax() {
        return "/heads hand <category> <head name>";
    }

    @Override
    public String getDescription() {
        return "Add a new head to the menu.";
    }
}
