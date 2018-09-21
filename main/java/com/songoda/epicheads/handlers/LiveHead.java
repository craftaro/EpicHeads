package com.songoda.epicheads.handlers;

import com.songoda.epicheads.EpicHeads;
import com.songoda.epicheads.api.EpicHeadsAPI;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class LiveHead {

	private int frames;
	private List<EpicHeadsAPI.Head> texures;
	private Location location;

	// Do not pay attention to this class, this is just a sort of sketch which is not ready.

	public LiveHead(int frames, List<EpicHeadsAPI.Head> texures, Location location /*.more.*/) {
		// Safety first, experimental features should not crash servers.
		if (frames > 60)
			frames = 60;
		this.frames = frames;
		if (texures.size() > frames)
			while (texures.size() != frames) {
				texures.remove(texures.size()); // logic - the last ones will be removed
			}
		this.texures = texures;
		this.location = location;
	}

	public void renderTexures() {
		int interval = frames / 20;
		new BukkitRunnable() {
			int fases;

			public void run() {
				// nessecary checks for head texures for fases.
				fases++;
				if (fases >= frames)
					fases = 0;

			}
		}.runTaskTimer(EpicHeads.getInstance(), 0, interval);
		// Render (but I am too tired for now).
		// TODO: External classes from the animation packages.
	}

}
