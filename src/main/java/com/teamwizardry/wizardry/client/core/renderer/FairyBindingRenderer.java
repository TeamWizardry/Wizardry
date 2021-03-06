package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.capability.player.miscdata.IMiscCapability;
import com.teamwizardry.wizardry.api.capability.player.miscdata.MiscCapabilityProvider;
import com.teamwizardry.wizardry.api.entity.fairy.FairyData;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
	public static void clientTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft().world == null) return;
		if (Minecraft.getMinecraft().player == null) return;
		if (event.side == Side.SERVER) return;
		if (event.phase != TickEvent.Phase.END) return;

		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;

		ItemStack stack = player.getHeldItemMainhand();

		if (stack.getItem() != ModItems.FAIRY_BELL) return;

		for (EntityFairy entityFairy : world.getEntities(EntityFairy.class, input -> {
			if (input == null) return false;
			FairyData dataFairy = input.getDataFairy();
			if (dataFairy == null) return false;
			return dataFairy.isDepressed;
		})) {
			Vec3d look = entityFairy.getLookTarget();
			if (look == null) continue;

			ParticleBuilder glitter = new ParticleBuilder(10);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFloatInOut(0.3f, 1f));

			glitter.setColor(Color.ORANGE);
			glitter.setCollision(true);
			glitter.disableRandom();
			glitter.disableMotionCalculation();

			if (ClientTickHandler.getTicks() % 10 == 0)
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(entityFairy.getPositionVector().add(0, entityFairy.height, 0)), 1, 0, (aFloat, particleBuilder) -> {

					particleBuilder.setScaleFunction(new InterpFloatInOut(0, 1f));
					particleBuilder.setAlphaFunction(new InterpFloatInOut(0.3f, 1f));
					particleBuilder.setScale(RandUtil.nextFloat(0.2f, 0.5f));
					particleBuilder.setLifetime(RandUtil.nextInt(10, 20));
					particleBuilder.setPositionFunction(new InterpLine(Vec3d.ZERO, look));
				});
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().world == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;
		if (Minecraft.getMinecraft().objectMouseOver == null) return;


		EntityPlayer player = Minecraft.getMinecraft().player;
		World world = Minecraft.getMinecraft().world;

		ItemStack stack = player.getHeldItemMainhand();

		if (stack.getItem() != ModItems.FAIRY_BELL) return;

		if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
			Entity entityHit = Minecraft.getMinecraft().objectMouseOver.entityHit;
			if (entityHit instanceof EntityFairy) {
				EntityFairy fairy = (EntityFairy) entityHit;
				FairyData data = fairy.getDataFairy();

				if (data != null && data.isDepressed) {
					double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
					double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
					double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

					IMiscCapability cap = MiscCapabilityProvider.getCap(Minecraft.getMinecraft().player);
					if (cap == null) return;

					UUID uuid = cap.getSelectedFairyUUID();
					if (uuid != null && uuid.equals(fairy.getUniqueID())) {
						GlStateManager.pushMatrix();
						GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

						RenderUtils.drawCircle(fairy.getPositionVector().add(0, fairy.height, 0), 0.3, true, false, Color.RED);
						GlStateManager.popMatrix();
					} else {
						GlStateManager.pushMatrix();
						GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

						RenderUtils.drawCircle(fairy.getPositionVector().add(0, fairy.height, 0), (Math.sin(ClientTickHandler.getTicks() / 5.0f) + 2.5) / 10.0, true, false);
						GlStateManager.popMatrix();
					}
				}
			}
		}

		for (EntityFairy entityFairy : world.getEntities(EntityFairy.class, input -> {
			if (input == null) return false;
			FairyData dataFairy = input.getDataFairy();
			if (dataFairy == null) return false;
			return dataFairy.isDepressed;
		})) {

			FairyData dataFairy = entityFairy.getDataFairy();
			if (dataFairy == null) return;

			IMiscCapability cap = MiscCapabilityProvider.getCap(Minecraft.getMinecraft().player);
			if (cap == null) return;

			double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
			double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
			double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();

			GlStateManager.glLineWidth(2f);

			GlStateManager.pushMatrix();
			GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

			RenderUtils.drawCircle(entityFairy.getPositionVector().add(0, entityFairy.height, 0), 0.25, true, false, Color.YELLOW);
			GlStateManager.popMatrix();

			Vec3d lookTarget = entityFairy.getLookTarget();
			Vec3d fairyPos = entityFairy.getPositionVector().add(0, entityFairy.height, 0);

			if (lookTarget != null) {
				Vec3d to = fairyPos.add(lookTarget);

				GlStateManager.pushMatrix();
				GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

				RenderUtils.drawCircle(to, 0.1, true, false, Color.ORANGE);

				GlStateManager.popMatrix();


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
				GlStateManager.enableDepth();

				bb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

				Color c = Color.ORANGE;

				bb.pos(to.x, to.y, to.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
				bb.pos(fairyPos.x, fairyPos.y, fairyPos.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();

				tess.draw();

				GlStateManager.enableTexture2D();
				GlStateManager.popMatrix();
			}

			UUID uuid = cap.getSelectedFairyUUID();
			if (uuid != null && uuid.equals(entityFairy.getUniqueID())) {

				boolean movingMode = NBTHelper.getBoolean(stack, "moving_mode", true);

				if (!movingMode) {

					Vec3d hitVec = Minecraft.getMinecraft().objectMouseOver.hitVec;
					Vec3d subtract = hitVec.subtract(fairyPos);
					double length = subtract.length();
					hitVec = entityFairy.getPositionVector().add(0, entityFairy.height, 0).add(subtract.normalize().scale(MathHelper.clamp(length, -3, 3)));

					GlStateManager.pushMatrix();
					GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

					RenderUtils.drawCircle(hitVec, 0.2, true, false, Color.CYAN);
					GlStateManager.popMatrix();

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
					GlStateManager.enableDepth();

					bb.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

					Color c = Color.GREEN;

					bb.pos(hitVec.x, hitVec.y, hitVec.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
					bb.pos(fairyPos.x, fairyPos.y, fairyPos.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();

					tess.draw();

					GlStateManager.enableTexture2D();
					GlStateManager.popMatrix();

				} else {
					if (Minecraft.getMinecraft().objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {

						BlockPos target = Minecraft.getMinecraft().objectMouseOver.getBlockPos();

						GlStateManager.pushMatrix();
						GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

						RenderUtils.drawCubeOutline(entityFairy.world, target, entityFairy.world.getBlockState(target), Color.CYAN);
						GlStateManager.popMatrix();


						Vec3d tar = new Vec3d(target).add(0.5, 0.5, 0.5).add(new Vec3d(Minecraft.getMinecraft().objectMouseOver.sideHit.getDirectionVec()));
						PathNavigate navigateFlying = entityFairy.getNavigator();
						Path path = navigateFlying.getPathToXYZ(tar.x, tar.y, tar.z);


						GlStateManager.pushMatrix();
						GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

						RenderUtils.drawCircle(tar, 0.25, true, false, Color.CYAN);
						GlStateManager.popMatrix();

						if (path != null) {
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
		glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
		glitter.disableRandom();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(pos), 1, 0, (i, build) -> {
			build.setScale(0.25f);
			build.setColor(color);
		});
	}
}
