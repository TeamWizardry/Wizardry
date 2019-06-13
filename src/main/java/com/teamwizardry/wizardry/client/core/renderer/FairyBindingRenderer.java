package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import com.teamwizardry.wizardry.api.entity.FairyObject;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.UUID;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
@SideOnly(Side.CLIENT)
public class FairyBindingRenderer {

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().world == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		for (EntityFairy entityFairy : Minecraft.getMinecraft().world.getEntities(EntityFairy.class, input -> {
			if (input == null) return false;
			FairyObject dataFairy = input.getDataFairy();
			if (dataFairy == null) return false;
			return dataFairy.isDepressed;
		})) {

			FairyObject dataFairy = entityFairy.getDataFairy();
			if (dataFairy == null) return;

			EntityPlayer player = Minecraft.getMinecraft().player;
			ItemStack stack = player.getHeldItemMainhand();

			if (stack.getItem() == ModItems.FAIRY_BELL) {
				IMiscCapability cap = MiscCapabilityProvider.getCap(Minecraft.getMinecraft().player);
				if (cap == null) return;

				UUID uuid = cap.getSelectedFairyUUID();
				if (uuid != null && uuid.equals(entityFairy.getUniqueID())) {
					double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
					double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
					double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

					GlStateManager.pushMatrix();
					GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

					RenderUtils.drawCircle(entityFairy.getPositionVector(), 0.25, true, false);

					GlStateManager.popMatrix();

					int color = MathHelper.hsvToRGB(ClientTickHandler.getTicks() % 200 / 200F, 0.6F, 1F);
					Color colorRGB = new Color(color);

					GlStateManager.glLineWidth(2f);

					if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
						BlockPos target = Minecraft.getMinecraft().objectMouseOver.getBlockPos();

						GlStateManager.pushMatrix();
						GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

						RenderUtils.drawCubeOutline(entityFairy.world, target, entityFairy.world.getBlockState(target));
						GlStateManager.popMatrix();


						Vec3d tar = new Vec3d(target).add(0.5, 0.5, 0.5).add(new Vec3d(Minecraft.getMinecraft().objectMouseOver.sideHit.getDirectionVec()));
						PathNavigate navigateFlying = entityFairy.getNavigator();
						Path path = navigateFlying.getPathToXYZ(tar.x, tar.y, tar.z);


						GlStateManager.pushMatrix();
						GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);


						RenderUtils.drawCircle(tar, 0.25, true, false);
						GlStateManager.popMatrix();

						if (path != null) {

							Tessellator tess = Tessellator.getInstance();
							BufferBuilder bb = tess.getBuffer();

							GlStateManager.pushMatrix();
							GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

							GlStateManager.disableLighting();
							GlStateManager.disableCull();
							GlStateManager.enableAlpha();
							GlStateManager.enableBlend();
							GlStateManager.shadeModel(GL11.GL_SMOOTH);
							GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
							GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
							GlStateManager.color(1, 1, 1, 1);
							GlStateManager.disableTexture2D();
							GlStateManager.enableColorMaterial();
							GlStateManager.translate(0, 0.01, 0);
							GlStateManager.disableDepth();

							bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);

							Color c = Color.GREEN;

							Vec3d lastPoint = null;
							Vec3d center = new Vec3d(entityFairy.posX, entityFairy.posY + entityFairy.height / 2.0, entityFairy.posZ);
							bb.pos(center.x, center.y, center.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();

							for (int i = 0; i < path.getCurrentPathLength(); i++) {

								Vec3d vec = path.getVectorFromIndex(entityFairy, i);

								if (lastPoint != null) {
									if (i >= path.getCurrentPathLength() - 1) {
										//	Vec3d from = path.getVectorFromIndex(entityFairy, i - 1);
										//	from = from.subtract(from.subtract(vec).scale(0.5));
//
										//	Vec3d fromControl = vec.subtract(from).normalize().scale(0.4);
//
										//	InterpBezier3D bezier = new InterpBezier3D(from, vec, fromControl);
//
										//	for (Vec3d dot : bezier.list(20)) {
										//		bb.pos(dot.x, dot.y, dot.z).color(1f, 0, 0, 1f).endVertex();
										//		lastPoint = dot;
										//	}
										continue;
									}

									Vec3d from = path.getVectorFromIndex(entityFairy, i - 1);
									from = from.subtract(from.subtract(vec).scale(0.5));

									Vec3d to = path.getVectorFromIndex(entityFairy, i + 1);
									to = to.subtract(to.subtract(vec).scale(0.5));

									Vec3d fromControl = vec.subtract(from).normalize().scale(0.4);
									Vec3d toControl = vec.subtract(to).normalize().scale(0.4);

									InterpBezier3D bezier = new InterpBezier3D(from, to, fromControl, toControl);

									for (Vec3d dot : bezier.list(20)) {
										bb.pos(dot.x, dot.y, dot.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
										lastPoint = dot;
									}
								} else {
									bb.pos(vec.x, vec.y, vec.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
									lastPoint = vec;
								}
							}

							bb.pos(tar.x, tar.y, tar.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();

							tess.draw();

							GlStateManager.enableTexture2D();
							GlStateManager.popMatrix();
						}
					}
				}
			}
		}
	}

	private static void light(World world, Vec3d pos, Color color) {
		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, build) -> {
			build.setScale(0.25f);
			build.setColor(color);
		});
	}
}
