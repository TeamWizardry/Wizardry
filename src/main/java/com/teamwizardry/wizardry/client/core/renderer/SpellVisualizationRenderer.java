package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.ICooldownSpellCaster;
import com.teamwizardry.wizardry.api.item.ISpellCaster;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class SpellVisualizationRenderer {

	@SubscribeEvent
	public static void tickDisplay(CustomWorldRenderEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().world == null) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;

		ItemStack hand = player.getHeldItemMainhand();
		if (!(hand.getItem() instanceof ISpellCaster)) return;
		if (hand.getItem() instanceof ICooldownSpellCaster && ((ICooldownSpellCaster) hand.getItem()).isCoolingDown(world, hand))
			return;

		List<SpellRing> chains = SpellUtils.getSpellChains(hand);


		for (SpellRing chain : chains) {

			SpellData data = new SpellData();
			data.processEntity(player, true);

			SpellRing ring = chain;
			while (ring != null && ring.getModule() != null) {

				ring.getModule().renderVisualization(world, data, ring, event.getPartialTicks());

				ring = ring.getChildRing();
			}
		}
	}
}
