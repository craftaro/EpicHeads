package net.sothatsit.heads.command.admin;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.command.AbstractCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.volatilecode.ItemNBT;
import net.sothatsit.heads.volatilecode.Items;
import net.sothatsit.heads.volatilecode.TextureGetter;
import net.sothatsit.heads.volatilecode.reflection.Version;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HandCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getHandCommand();
    }

    @Override
    public String getPermission() {
        return "heads.hand";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Hand.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }

        if (args.length < 3) {
            sendInvalidArgs(sender);
            return true;
        }

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            nameBuilder.append(' ');
            nameBuilder.append(args[i]);
        }

        String name = nameBuilder.toString().substring(1);
        String category = args[1];

        if (category.length() > 32) {
            Lang.Command.Hand.categoryLength(category).send(sender);
            return true;
        }

        Player player = (Player) sender;

        ItemStack hand = player.getInventory().getItemInMainHand();

        if(!Items.isSkull(hand)) {
            Lang.Command.Hand.notSkull().send(sender);
            return true;
        }

        String texture = ItemNBT.getTextureProperty(hand);

        if (texture == null || texture.isEmpty()) {
            Lang.Command.Hand.noTextureProperty().send(sender);

            if (Version.v1_8.higherThan(Version.getVersion())) {
                Lang.Command.Hand.notSupported().send(sender);
                return true;
            }

            SkullMeta meta = (SkullMeta) hand.getItemMeta();

            final String owner = meta.getOwner();

            if (owner == null || owner.isEmpty()) {
                Lang.Command.Hand.noNameProperty().send(sender);
                return true;
            }

            texture = TextureGetter.getCachedTexture(owner);

            if (texture == null || texture.isEmpty()) {
                Lang.Command.Hand.fetching().send(sender);
                TextureGetter.getTexture(owner, (resolvedTexture) -> {
                    if (resolvedTexture == null || resolvedTexture.isEmpty()) {
                        Lang.Command.Hand.cantFind(owner).send(sender);
                        return;
                    }

                    add(sender, category, name, resolvedTexture);
                });
                return true;
            }
        }

        add(sender, category, name, texture);
        return true;
    }

    public void add(CommandSender sender, String category, String name, String texture) {
        CacheHead head = new CacheHead(name, category, texture);
        
        Heads.getCache().addHead(head);
        Heads.getInstance().saveCache();
        
        Lang.Command.Hand.adding(name, category).send(sender);
    }
    
}
