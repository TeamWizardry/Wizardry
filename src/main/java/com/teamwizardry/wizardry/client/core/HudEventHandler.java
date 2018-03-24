package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.librarianlib.features.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Demoniaque on 6/20/2016.
 */
public class HudEventHandler extends Gui {

	private final Texture HUD_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/hud.png"));
	private final Sprite emptyManaBar = HUD_TEXTURE.getSprite("mana_empty", 101, 5);
	private final Sprite fullManaBar = HUD_TEXTURE.getSprite("mana_full", 101, 5);
	private final Sprite emptyBurnoutBar = HUD_TEXTURE.getSprite("burnout_empty", 101, 5);
	private final Sprite fullBurnoutBar = HUD_TEXTURE.getSprite("burnout_full", 101, 5);

	@SubscribeEvent
	public void renderHud(Post event) {
		ScaledResolution resolution = event.getResolution();
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		EntityPlayer player = Minecraft.getMinecraft().player;

		ItemStack stack = BaublesSupport.getItem(player, ModItems.FAKE_HALO, ModItems.CREATIVE_HALO, ModItems.REAL_HALO);
		if (stack == null || stack.isEmpty()) return;

		if (event.getType() == ElementType.EXPERIENCE) {

			HUD_TEXTURE.bind();

			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int right = ((width / 2) - (100 / 2)) + 145;
			int top = height - 17;
			emptyManaBar.draw(ClientTickHandler.getTicks(), right, top);
			emptyBurnoutBar.draw(ClientTickHandler.getTicks(), right, top + 6);
			GlStateManager.popMatrix();

			CapManager manager = new CapManager(player);

			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int visualManaLength = 0;
			if (manager.getMana() > 0)
				visualManaLength = (int) (((manager.getMana() * 100) / manager.getMaxMana()) % 101);
			fullManaBar.drawClipped(ClientTickHandler.getTicks(), right, top, visualManaLength, 5);

			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int visualBurnoutLength = 0;
			if (manager.getBurnout() > 0)
				visualBurnoutLength = (int) (((manager.getBurnout() * 100) / manager.getMaxBurnout()) % 101);
			fullBurnoutBar.drawClipped(ClientTickHandler.getTicks(), right, top + 6, visualBurnoutLength, 5);
			GlStateManager.popMatrix();
		}
	}
}
