package net.sothatsit.heads.command.user;

import java.util.UUID;

import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.command.AbstractCommand;
import net.sothatsit.heads.config.MainConfig;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.volatilecode.Items;
import net.sothatsit.heads.volatilecode.TextureGetter;
import net.sothatsit.heads.volatilecode.reflection.Version;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GetCommand extends AbstractCommand {

    @Override
    public String getCommandLabel(MainConfig config) {
        return config.getGetCommand();
    }

    @Override
    public String getPermission() {
        return "heads.get";
    }

    @Override
    public Lang.HelpSection getHelp() {
        return Lang.Command.Get.help();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length != 2) {
            sendInvalidArgs(sender);
            return true;
        }
        
        if (Version.v1_8.higherThan(Version.getVersion())) {
            Lang.Command.Get.oldMethod().send(sender);

            ItemStack head = Items.createSkull().build();
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            
            meta.setOwner(args[1]);
            meta.setDisplayName(Lang.Command.Get.headName(args[1]).getSingle());
            
            head.setItemMeta(meta);
            
            Lang.Command.Get.adding(args[1]).send(sender);
            ((Player) sender).getInventory().addItem(head);
            return true;
        }

        String texture = TextureGetter.getCachedTexture(args[1]);

        if (texture != null) {
            giveHead((Player) sender, args[1], texture);
            return true;
        }

        Lang.Command.Get.fetching().send(sender);

        final UUID uuid = ((Player) sender).getUniqueId();
        final String name = args[1];

        TextureGetter.getTexture(name, (resolvedTexture) -> {
            giveHead(Bukkit.getPlayer(uuid), name, resolvedTexture);
        });
        return true;
    }

    public void giveHead(Player player, String name, String texture) {
        if (player != null) {
            if (texture == null || texture.isEmpty()) {
                Lang.Command.Get.cantFind(name).send(player);
                return;
            }

            CacheHead head = new CacheHead(name, "getcommand", texture);

            Lang.Command.Get.adding(name).send(player);

            player.getInventory().addItem(head.getItemStack());
        }
    }
}
