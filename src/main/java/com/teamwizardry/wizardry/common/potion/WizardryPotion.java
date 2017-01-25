package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by LordSaad.
 */
public class WizardryPotion extends Potion {
	public static ResourceLocation texture = new ResourceLocation(Wizardry.MODID, "textures/misc/potions.png");
	private final int iconIndex;

	public WizardryPotion(String name, boolean badEffect, int color, int iconIndex) {
		super(badEffect, color);
		GameRegistry.register(this, new ResourceLocation(Wizardry.MODID, name));
		setPotionName(Wizardry.MODID + ".potion." + name);
		this.iconIndex = iconIndex;
	}

	public boolean hasEffect(EntityLivingBase entity) {
		return hasEffect(entity, this);
	}

	public boolean hasEffect(EntityLivingBase entity, Potion potion) {
		return entity.getActivePotionEffect(potion) != null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		render(x + 6, y + 7, 1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
		render(x + 3, y + 3, alpha);
	}

	@SideOnly(Side.CLIENT)
	private void render(int x, int y, float alpha) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		GlStateManager.color(1, 1, 1, alpha);

		int textureX = iconIndex % 8 * 18;
		int textureY = 198 + iconIndex / 8 * 18;

		vb.pos(x, y + 18, 0).tex(textureX * 0.00390625, (textureY + 18) * 0.00390625).endVertex();
		vb.pos(x + 18, y + 18, 0).tex((textureX + 18) * 0.00390625, (textureY + 18) * 0.00390625).endVertex();
		vb.pos(x + 18, y, 0).tex((textureX + 18) * 0.00390625, textureY * 0.00390625).endVertex();
		vb.pos(x, y, 0).tex(textureX * 0.00390625, textureY * 0.00390625).endVertex();

		tessellator.draw();
	}
}
