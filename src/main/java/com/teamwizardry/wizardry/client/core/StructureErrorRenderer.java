package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
public class StructureErrorRenderer {

	private static final Sprite particle = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/particles/sparkle_blurred.png"));
	public static StructureErrorRenderer INSTANCE = new StructureErrorRenderer();
	private ArrayList<ParticleError> errors = new ArrayList<>();

	private StructureErrorRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addError(BlockPos pos) {
		for (ParticleError error : errors) {
			if (error == null) continue;
			if (error.pos.toLong() == pos.toLong())
				return;
		}

		errors.add(new ParticleError(pos, 100));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		ArrayList<ParticleError> tmp = new ArrayList<>(errors);

		for (ParticleError error : tmp) {
			error.tick -= 1;
			if (error.tick <= 0) errors.remove(error);

			GlStateManager.pushMatrix();
			EntityPlayer player = Minecraft.getMinecraft().player;

			double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
			double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
			double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();


			GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);
			GlStateManager.translate(error.pos.getX() + 0.5f, error.pos.getY() + 0.5f, error.pos.getZ() + 0.5f);

			GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float) (Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.25, -0.25, -0.25);

			GlStateManager.disableDepth();

			GlStateManager.disableCull();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
			GlStateManager.color(1f, 0, 0, (float) (error.tick / 100.0));

			particle.getTex().bind();
			particle.draw((int) event.getPartialTicks(), 0, 0, 0.5f, 0.5f);

			GlStateManager.enableDepth();

			GlStateManager.popMatrix();
		}
	}

	class ParticleError {

		public final BlockPos pos;
		public int tick;

		public ParticleError(BlockPos pos, int maxTick) {
			this.pos = pos;
			this.tick = maxTick;
		}
	}
}
