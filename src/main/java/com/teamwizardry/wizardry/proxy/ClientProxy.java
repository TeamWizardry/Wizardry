package com.teamwizardry.wizardry.proxy;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.client.CustomBlockMapSprites;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.client.core.CapeHandler;
import com.teamwizardry.wizardry.client.core.CooldownHandler;
import com.teamwizardry.wizardry.client.core.HudEventHandler;
import com.teamwizardry.wizardry.client.core.LightningRenderer;
import com.teamwizardry.wizardry.client.render.BloodRenderLayer;
import com.teamwizardry.wizardry.common.core.version.VersionChecker;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModEntities;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {

	// Trigger hand swinging when on staff cooldowns
	private static Function2<ItemRenderer, Object, Unit> itemStackMainHandHandler = MethodHandleHelper.wrapperForSetter(ItemRenderer.class, "d", "field_187467_d", "itemStackMainHand");
	private static Function2<ItemRenderer, Object, Unit> itemStackOffHandHandler = MethodHandleHelper.wrapperForSetter(ItemRenderer.class, "e", "field_187468_e", "itemStackOffHand");

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		MinecraftForge.EVENT_BUS.register(new ModBlocks());
		MinecraftForge.EVENT_BUS.register(new HudEventHandler());
		if (ConfigValues.versionCheckerEnabled)
			MinecraftForge.EVENT_BUS.register(VersionChecker.INSTANCE);

		ModEntities.initModels();

		LightningRenderer.INSTANCE.getClass();
		CooldownHandler.INSTANCE.getClass();

		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal_ring"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal_ring_outer"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/pearl"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_orb"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_pearl_cube"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/nacre_pearl_cube"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		MinecraftForge.EVENT_BUS.register(CapeHandler.instance());

		Minecraft.getMinecraft().getRenderManager().getSkinMap().values().forEach(render ->
			render.addLayer(new BloodRenderLayer(render))
		);

	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	@Override
	public void setItemStackHandHandler(EnumHand hand, ItemStack stack) {
		if (hand == EnumHand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getMinecraft().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getMinecraft().getItemRenderer(), stack);
	}

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		MinecraftForge.EVENT_BUS.post(new ResourceReloadEvent(resourceManager));
	}

	public static class ResourceReloadEvent extends Event {
		public final IResourceManager resourceManager;

		public ResourceReloadEvent(IResourceManager manager) {
			resourceManager = manager;
		}
	}
	
	@Override
	public void tileLightParticles(World world, BlockPos pos)
	{
		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
		glitter.setColorFunction(new InterpColorHSV(Color.CYAN, Color.BLUE));
		glitter.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(1, 3), 0));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), 1, 0, (i, build) -> {
			build.setMotion(new Vec3d(
					RandUtil.nextDouble(-0.01, 0.01),
					RandUtil.nextDouble(0, 0.03),
					RandUtil.nextDouble(-0.01, 0.01)));
		});
	}
	
	@Override
	public void tileManaSinkParticles(World world, BlockPos pos, BlockPos faucetPos)
	{
		ParticleBuilder helix = new ParticleBuilder(200);
		helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		helix.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));

		ParticleSpawner.spawn(helix, world, new StaticInterp<>(new Vec3d(faucetPos).addVector(0.5, 1, 0.5)), 1, 0, (aFloat, particleBuilder) -> {
			helix.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
			helix.setScale(RandUtil.nextFloat());
			helix.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(pos.subtract(faucetPos)), new Vec3d(0, 20, 0), new Vec3d(0, 5, 0)));
			helix.setLifetime(RandUtil.nextInt(10, 40));
		});
	}
}
