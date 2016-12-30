package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.client.core.ClientTickHandler;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
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
 * Created by Saad on 6/20/2016.
 */
public class HudEventHandler extends Gui {

	private final Texture HUD_TEXTURE = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/hud.png"));
	private final Sprite emptyManaBar = HUD_TEXTURE.getSprite("mana_empty", 101, 5);
	private final Sprite fullManaBar = HUD_TEXTURE.getSprite("mana_full", 101, 5);
	private final Sprite emptyBurnoutBar = HUD_TEXTURE.getSprite("burnout_empty", 101, 5);
	private final Sprite fullBurnoutBar = HUD_TEXTURE.getSprite("burnout_full", 101, 5);

	@SubscribeEvent
	public void renderHud(Post event) {
		Minecraft mc = Minecraft.getMinecraft();
        ItemStack stack = Minecraft.getMinecraft().player.getActiveItemStack();

		ScaledResolution resolution = event.getResolution();
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
        EntityPlayer player = Minecraft.getMinecraft().player;

		if (event.getType() == ElementType.EXPERIENCE) {

			HUD_TEXTURE.bind();

			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int right = ((width / 2) - (100 / 2)) + 145;
			int top = height - 20;
			emptyManaBar.draw(ClientTickHandler.getTicks(), right, top);
			emptyBurnoutBar.draw(ClientTickHandler.getTicks(), right, top + 6);
			GlStateManager.popMatrix();

			//WizardryData data = DataFactory.INSTANCE.getPlayerData(player)
			IWizardryCapability data = WizardryCapabilityProvider.get(player);

			//if (data == null) return;

			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int visualManaLength = 0;
			if (data.getMana() > 0) visualManaLength = ((data.getMana() * 100) / data.getMaxMana()) % 101;
			fullManaBar.drawClipped(ClientTickHandler.getTicks(), right, top, visualManaLength, 5);

			GlStateManager.color(1.0F, 1.0F, 1.0F);
			int visualBurnoutLength = 0;
			if (data.getBurnout() > 0) visualBurnoutLength = ((data.getBurnout() * 100) / data.getMaxBurnout()) % 101;
			fullBurnoutBar.drawClipped(ClientTickHandler.getTicks(), right, top + 6, visualBurnoutLength, 5);
			GlStateManager.popMatrix();
		}
	}
}
