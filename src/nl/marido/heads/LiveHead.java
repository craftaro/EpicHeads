package nl.marido.heads;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class LiveHead {

	private int frames;
	private List<Heads> texures;
	private Location location;

	// Do not pay attention to this class, this is just a sort of sketch which is not ready.

	public LiveHead(int frames, List<Heads> texures, Location location /*.more.*/) {
		// Safety first, experimental features should not crash servers.
		if (frames > 60)
			frames = 60;
		this.frames = frames;
		if (texures.size() > frames)
			while (texures.size() != frames) {
				texures.remove(0);
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
		}.runTaskTimer(Heads.getInstance(), 0, interval);
		// Render (but I am too tired for now).
		// TODO: External classes from the animation packages.
	}

}
