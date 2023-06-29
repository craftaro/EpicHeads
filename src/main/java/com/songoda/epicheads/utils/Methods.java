package com.songoda.epicheads.utils;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.utils.TextUtils;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.settings.Settings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Methods {
    public static ItemStack createToken(int amount) {
        ItemStack itemStack = new ItemStack(Material.valueOf(Settings.ITEM_TOKEN_TYPE.getString()));

        if (itemStack.getType() == (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                ? Material.PLAYER_HEAD : Material.valueOf("SKULL_ITEM"))) {
            itemStack = EpicHeads.getInstance().getHeadManager().getHeads().stream()
                    .filter(head -> head.getId() == Settings.ITEM_TOKEN_ID.getInt())
                    .findFirst().get().asItemStack();
        }
        itemStack.setAmount(amount);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(TextUtils.formatText(Settings.ITEM_TOKEN_NAME.getString()));
        List<String> lore = new ArrayList<>();
        for (String line : Settings.ITEM_TOKEN_LORE.getStringList()) {
            if (!line.equals("")) {
                lore.add(TextUtils.formatText(line));
            }
        }
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
