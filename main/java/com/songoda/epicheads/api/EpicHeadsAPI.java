package com.songoda.epicheads.api;

import com.google.common.collect.ImmutableList;
import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.cache.CacheHead;
import com.songoda.epicheads.util.Checks;
import com.songoda.epicheads.volatilecode.TextureGetter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EpicHeadsAPI {

	public static class Head {

		private final CacheHead head;

		private Head(CacheHead head) {
			Checks.ensureNonNull(head, "head");
			this.head = head;
		}

		public boolean isEnabled() {
			return EpicHeads.getInstance() != null;
		}

		public int getId() {
			return head.getId();
		}

		public String getName() {
			return head.getName();
		}

		public String getCategory() {
			return head.getCategory();
		}

		public double getCost() {
			return head.getCost();
		}

		public ItemStack getItem() {
			return head.getItemStack();
		}

		public ItemStack getItem(String displayName) {
			return head.getItemStack(displayName);
		}

		private static Head fromCacheHead(CacheHead head) {
			return (head == null ? null : new Head(head));
		}

		private static Head fromNameAndTexture(String name, String texture) {
			return (texture == null ? null : fromCacheHead(new CacheHead(name, "EpicHeadsAPI", texture)));
		}

		private static List<Head> fromCacheHeads(List<CacheHead> heads) {
			ImmutableList.Builder<Head> converted = ImmutableList.builder();
			for (CacheHead head : heads) {
				converted.add(Head.fromCacheHead(head));
			}
			return converted.build();
		}

	}

	public static Head getHead(int id) {
		CacheHead head = EpicHeads.getInstance().getCache().findHead(id);
		if (head == null)
			return null;
		return new Head(head);
	}

	@Deprecated
	public static List<Head> searchHeads(String query) {
		List<CacheHead> search = EpicHeads.getInstance().getCache().searchHeads(query);
		return Head.fromCacheHeads(search);
	}

	public static void searchHeads(String query, Consumer<List<Head>> onResult) {
		EpicHeads.getInstance().getCache().searchHeadsAsync(query, heads -> {
			onResult.accept(Head.fromCacheHeads(heads));
		});
	}

	public static Set<String> getCategories() {
		return EpicHeads.getInstance().getCache().getCategories();
	}

	public static List<Head> getCategoryHeads(String category) {
		List<CacheHead> categoryHeads = EpicHeads.getInstance().getCache().getCategoryHeads(category);
		return Head.fromCacheHeads(categoryHeads);
	}

	public static List<Head> getAllHeads() {
		List<CacheHead> heads = EpicHeads.getInstance().getCache().getHeads();
		return Head.fromCacheHeads(heads);
	}

	public static void downloadHead(String playerName, Consumer<Head> consumer) {
		TextureGetter.getTexture(playerName, (texture) -> {
			consumer.accept(Head.fromNameAndTexture(playerName, texture));
		});
	}

}