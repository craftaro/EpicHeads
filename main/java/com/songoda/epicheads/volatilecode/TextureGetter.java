package com.songoda.epicheads.volatilecode;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.util.Checks;
import com.songoda.epicheads.util.SafeCall;
import com.songoda.epicheads.volatilecode.reflection.nms.MinecraftServer;
import com.songoda.epicheads.volatilecode.reflection.nms.TileEntitySkull;

import java.util.Iterator;
import java.util.function.Consumer;

public class TextureGetter {

	public static String getCachedTexture(String name) {
		GameProfile profile = MinecraftServer.getServer().getUserCache().getCachedProfile(name);

		return findTexture(profile);
	}

	public static void getTexture(String name, Consumer<String> callback) {
		Checks.ensureNonNull(name, "name");
		Checks.ensureNonNull(callback, "callback");

		Consumer<String> safeCallback = SafeCall.consumer(callback, "callback");

		String cachedTexture = getCachedTexture(name);

		if (cachedTexture != null) {
			callback.accept(cachedTexture);
			return;
		}

		TileEntitySkull.resolveTexture(name, profile -> {
			EpicHeads.sync(() -> safeCallback.accept(findTexture(profile, true)));

			return true;
		});
	}

	public static String findTexture(GameProfile profile) {
		if (profile == null || !profile.isComplete())
			return null;

		PropertyMap properties = profile.getProperties();
		if (properties == null || !properties.containsKey("textures"))
			return null;

		Iterator<Property> iterator = properties.get("textures").iterator();

		return (iterator.hasNext() ? iterator.next().getValue() : null);
	}

	private static String findTexture(GameProfile profile, boolean cacheProfile) {
		String texture = findTexture(profile);

		if (cacheProfile && texture != null) {
			MinecraftServer.getServer().getUserCache().addProfile(profile);
		}

		return texture;
	}

}
