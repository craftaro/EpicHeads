package net.sothatsit.heads;

import java.util.List;

import org.bukkit.Location;

public class LiveHead {

	private int frames;
	private List<Heads> fases;
	private Location location;

	public LiveHead(int frames, List<Heads> fases, Location location /*.more.*/) {
		// Safety first, experimental features should not crash servers.
		if (frames > 60)
			frames = 60;
		this.frames = frames;
		this.fases = fases;
		this.location = location;
	}

	public void renderTexure() {
		int interval = frames / 20;
		// Render (but I am too tired for now).
		// TODO: External classes from the animation packages.
	}

}
