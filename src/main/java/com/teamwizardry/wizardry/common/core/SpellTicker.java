package com.teamwizardry.wizardry.common.core;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.module.Module;
import kotlin.Pair;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LordSaad.
 */
// TODO: use world time instead of ticking
public class SpellTicker {

	public static SpellTicker INSTANCE = new SpellTicker();

	public HashMap<Module, Pair<SpellData, Integer>> ticker = new HashMap<>();

	private SpellTicker() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void tick(TickEvent.WorldTickEvent event) {
		if (event.world.isRemote) return;

		ArrayList<Module> modules = new ArrayList<>(ticker.keySet());
		modules.forEach(module -> {
			int time = ticker.get(module).getSecond();
			if (time > 0) {
				ticker.put(module, new Pair<>(ticker.get(module).getFirst().copy(), --time));
				SpellData spell = ticker.get(module).getFirst();
				spell.addData(SpellData.DefaultKeys.TIME_LEFT, time);
				module.castSpell(ticker.get(module).getFirst());
			} else {
				ticker.remove(module);
			}
		});
	}
}
