package com.teamwizardry.wizardry.api.arena;

import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class ArenaManager extends TimerTask {

	public static ArenaManager INSTANCE = new ArenaManager();

	private HashSet<Arena> arenas = new HashSet<>();

	private ArenaManager() {
		new Timer().scheduleAtFixedRate(INSTANCE, 0, 1);
	}

	public Arena addArena(Arena arena) {
		arenas.add(arena);
		return arena;
	}

	@Override
	public void run() {
		HashSet<Arena> trash = new HashSet<>();
		for (Arena arena : arenas) {
			if (arena == null) continue;
			if (arena.hasEnded()) {
				trash.add(arena);
			}
			if (!arena.isActive()) continue;
			if (!arena.sanityCheck()) {
				arena.dealWithStructureConflict();
				continue;
			}

			arena.tick(System.currentTimeMillis() - arena.getStartTick());
		}

		arenas.removeAll(trash);
	}
}
