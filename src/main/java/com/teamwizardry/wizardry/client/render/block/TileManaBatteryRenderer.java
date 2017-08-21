package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.fluid.BlockFluidMana;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.lib.LibParticles;
import com.teamwizardry.wizardry.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Created by LordSaad.
 */
public class TileManaBatteryRenderer extends TileEntitySpecialRenderer<TileManaBattery> {

	private IBakedModel modelRing, modelCrystal, modelRingOuter;

	public TileManaBatteryRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void reload(ClientProxy.ResourceReloadEvent event) {
		modelRing = null;
		modelCrystal = null;
		modelRingOuter = null;
	}

	private void getBakedModels() {
		IModel model;
		if (modelRing == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal_ring"));
				modelRing = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (modelRingOuter == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal_ring_outer"));
				modelRingOuter = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (modelCrystal == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/mana_crystal"));
				modelCrystal = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
						location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void render(TileManaBattery te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		World world = te.getWorld();

		int count = 0;
		for (int i = -4; i < 4; i++)
			for (int j = -4; j < 4; j++)
				if (world.getBlockState(te.getPos().add(i, -3, j)) == BlockFluidMana.instance.getDefaultState())
					count++;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		getBakedModels();

		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		if (Minecraft.isAmbientOcclusionEnabled())
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		else GlStateManager.shadeModel(GL11.GL_FLAT);

		GlStateManager.translate(x, y + 0.5, z);
		GlStateManager.disableRescaleNormal();

		GlStateManager.translate(0, Math.sin((te.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks()) / 40) / 8, 0);
		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelCrystal, 1.0F, 1, 1, 1);

		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(te.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks(), 0, 1, 0);
		GlStateManager.translate(-0.5, 0, -0.5);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelRing, 1.0F, 1, 1, 1);

		GlStateManager.translate(0.5, 0, 0.5);
		GlStateManager.rotate(te.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks(), 0, -1, 0);
		GlStateManager.rotate(te.getWorld().getTotalWorldTime() + ClientTickHandler.getPartialTicks(), 0, -1, 0);
		GlStateManager.translate(-0.5, 0, -0.5);

		Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightnessColor(modelRingOuter, 1.0F, 1, 1, 1);

		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

		if (te.getBlockType() instanceof IStructure)
			if (!((IStructure) te.getBlockType()).renderBoundries(te.getWorld(), te.getPos())) return;

		PosUtils.ManaBatteryPositions positions = new PosUtils.ManaBatteryPositions(world, te.getPos());
		for (BlockPos pos : positions.fullCircle)
			if (RandUtil.nextInt(5) == 0)
				LibParticles.MAGIC_DOT(world, new Vec3d(pos).addVector(0.5, 0.5, 0.5), -1);

		CapManager manager = new CapManager(te.cap);
		if (!manager.isManaFull()) {
			if (count >= 21)
				for (BlockPos pos : positions.takenPoses) {
					if (RandUtil.nextInt(7) == 0)
						LibParticles.COLORFUL_BATTERY_BEZIER(world, pos, te.getPos());
				}
		}

		if (RandUtil.nextInt(10) == 0) {
			ParticleBuilder glitter = new ParticleBuilder(3);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setColorFunction(new InterpColorHSV(ColorUtils.changeColorAlpha(Color.CYAN, RandUtil.nextInt(50, 150)), ColorUtils.changeColorAlpha(Color.BLUE, RandUtil.nextInt(50, 150))));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(te.getPos()).addVector(0.5, 0.5, 0.5)), RandUtil.nextInt(1, 3), 0, (aFloat, particleBuilder) -> {
				glitter.setAlphaFunction(new InterpFadeInOut(1f, 1f));
				glitter.setMotion(new Vec3d(
						RandUtil.nextDouble(-0.05, 0.05),
						RandUtil.nextDouble(-0.1, 0.1),
						RandUtil.nextDouble(-0.05, 0.05)
				));
				glitter.setLifetime(RandUtil.nextInt(30));
				glitter.setScale((float) RandUtil.nextDouble(3));
			});
		}
	}
}
