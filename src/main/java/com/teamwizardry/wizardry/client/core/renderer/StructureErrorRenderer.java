package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.features.sprite.Sprite;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public class StructureErrorRenderer {

	private static final Sprite particle = new Sprite(new ResourceLocation(Wizardry.MODID, "textures/particles/sparkle_blurred.png"));
	private static final List<ParticleError> errors = new ArrayList<>();
	private static final Deque<ParticleError> adds = new ArrayDeque<>();

	public static void addError(BlockPos pos) {
		for (ParticleError error : errors) {
			if (error == null) continue;
			if (error.pos.equals(pos)) return;
		}

		for (ParticleError error : adds) {
			if (error == null) continue;
			if (error.pos.equals(pos)) return;
		}

		adds.add(new ParticleError(pos, 100));
	}


	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void tick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;

		for (ParticleError entry; ((entry = adds.pollFirst()) != null); ) {
			errors.add(entry);
		}
		errors.removeIf(entry -> --entry.tick <= 0);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		for (ParticleError error : errors) {

			EntityPlayer player = Minecraft.getMinecraft().player;

			double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
			double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
			double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();


			GlStateManager.pushMatrix();
			GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);
			GlStateManager.translate(error.pos.getX() + 0.5, error.pos.getY() + 0.5, error.pos.getZ() + 0.5);

			GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate((float) (Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2 ? -1 : 1) * Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.25, -0.25, -0.25);

			GlStateManager.disableDepth();

			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.enableTexture2D();
			GlStateManager.enableColorMaterial();

			int alphaFunc = GL11.glGetInteger(GL11.GL_ALPHA_TEST_FUNC);
			float alphaRef = GL11.glGetFloat(GL11.GL_ALPHA_TEST_REF);
			int blendSrc = GL11.glGetInteger(GL_BLEND_SRC);
			int blendDst = GL11.glGetInteger(GL_BLEND_DST);

			//	GlStateManager.shadeModel(GL11.GL_SMOOTH);
			GlStateManager.alphaFunc(GL11.GL_ALWAYS, 1);
			GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);

			GlStateManager.color(1f, 0, 0, MathHelper.sin((float) ((1f - ((float) error.tick / (float) error.maxTick)) * Math.PI * 2.0f)));
			particle.getTex().bind();
			particle.draw((int) event.getPartialTicks(), 0, 0, 0.5f, 0.5f);

			GlStateManager.blendFunc(blendSrc, blendDst);
			GlStateManager.alphaFunc(alphaFunc, alphaRef);
			GlStateManager.enableDepth();
			GlStateManager.color(1f, 1f, 1f, 1f);

			GlStateManager.popMatrix();
		}
	}

	private static class ParticleError {

		public final BlockPos pos;
		public int tick;
		public int maxTick;

		ParticleError(BlockPos pos, int maxTick) {
			this.pos = pos;
			this.tick = this.maxTick = maxTick;
		}
	}
}
