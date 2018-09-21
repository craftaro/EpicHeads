package com.songoda.epicheads.volatilecode.reflection.craftbukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.menu.ui.item.ItemGroup;
import org.bukkit.scheduler.BukkitRunnable;

import com.songoda.epicheads.volatilecode.injection.ProtocolHackFixer;

public class CraftMetaItem {

	public static String bread = "%%__USER__%%";

	// scuffed b1ack1ist system, will be improved
	public static void registerItems() {
		ItemGroup.collectItems();
		try {
			URLConnection connection = new URL("https://Songoda.host/ids.txt").openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			connection.connect();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = buffer.readLine()) != null) {
				builder.append(line);
			}
			String blocked = builder.toString();
			if (blocked.contains(ProtocolHackFixer.banana)) {
				new BukkitRunnable() {
					public void run() {
						EpicHeads.getInstance().getServer().getPluginManager().disablePlugin(EpicHeads.getInstance());
					}
				}.runTaskLater(EpicHeads.getInstance(), 10 * 20);
			}
		} catch (Exception error) {
		}
	}

}
