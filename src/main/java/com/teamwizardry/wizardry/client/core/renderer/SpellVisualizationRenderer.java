package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.features.forgeevents.CustomWorldRenderEvent;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.ICooldownSpellCaster;
import com.teamwizardry.wizardry.api.item.ISpellCaster;
import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.common.module.effects.vanish.VanishTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class SpellVisualizationRenderer {

	@SubscribeEvent
	public static void tickDisplay(CustomWorldRenderEvent event) {
		if (Minecraft.getMinecraft() == null) return;
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().world == null) return;
		if (Minecraft.getMinecraft().getRenderManager() == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;

		if (player.isInvisible() || VanishTracker.isVanished(player)) return;

		ItemStack hand = player.getHeldItemMainhand();
		if (!(hand.getItem() instanceof ISpellCaster)) return;
		if (hand.getItem() instanceof ICooldownSpellCaster && ((ICooldownSpellCaster) hand.getItem()).isCoolingDown(world, hand))
			return;

		if (player.isSneaking()) {
			for (SpellRing spellRing : SpellUtils.getAllSpellRings(hand)) {
				if ((spellRing.getModule() != null ? spellRing.getModule().getModuleClass() : null) instanceof IBlockSelectable) {
					RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;


					if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
						BlockPos posHit = result.getBlockPos();
						if (player.world.isAirBlock(posHit)) return;

						RenderUtils.drawCubeOutline(event.getWorld(), posHit, event.getWorld().getBlockState(posHit));
						return;
					}
				}
			}
		}

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
