package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.ICooldownSpellCaster;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class SpellVisualizationRenderer {

	private static HashMap<ModuleInstance, SpellData> previousTickCache = new HashMap<>();

	@SubscribeEvent
	public static void tickDisplay(CustomWorldRenderEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().world == null) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;

		ItemStack hand = player.getHeldItemMainhand();
		if (hand.isEmpty() || hand.getItem() != ModItems.STAFF) return;
		if (hand.getItem() instanceof ICooldownSpellCaster && ((ICooldownSpellCaster) hand.getItem()).isCoolingDown(world, hand))
			return;

		List<SpellRing> chains = SpellUtils.getSpellChains(hand);

		Set<ModuleInstance> untransientModules = new HashSet<>();

		for (SpellRing chain : chains) {

			SpellData data = new SpellData(world);
			data.processEntity(player, true);

			SpellRing ring = chain;
			while (ring != null) {
				if (ring.getModule() != null) {
					if (previousTickCache.containsKey(ring.getModule())) {
						untransientModules.add(ring.getModule());

						SpellData oldTickData = previousTickCache.get(ring.getModule());
						SpellData newTickData = ring.getModule().renderVisualization(data, ring, oldTickData);

						if (newTickData != oldTickData) {
							previousTickCache.put(ring.getModule(), newTickData);
						}
					} else {

						SpellData newTickData = ring.getModule().renderVisualization(data, ring, new SpellData(world));
						previousTickCache.put(ring.getModule(), newTickData);
					}
				}
				ring = ring.getChildRing();
			}
		}

		Set<ModuleInstance> tmp = new HashSet<>(previousTickCache.keySet());
		for (ModuleInstance module : tmp) {
			if (!untransientModules.contains(module)) {
				previousTickCache.remove(module);
			}
		}
	}
}
