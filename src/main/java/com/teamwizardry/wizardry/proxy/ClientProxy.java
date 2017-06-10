package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.utilities.client.CustomBlockMapSprites;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.client.core.CapeHandler;
import com.teamwizardry.wizardry.client.core.HudEventHandler;
import com.teamwizardry.wizardry.client.core.WizardryClientMethodHandles;
import com.teamwizardry.wizardry.client.fx.Shaders;
import com.teamwizardry.wizardry.client.render.BloodRenderLayer;
import com.teamwizardry.wizardry.client.render.LightningRenderer;
import com.teamwizardry.wizardry.common.core.version.VersionChecker;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModEntities;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.util.Map;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {

	private static Function2<ItemRenderer, Object, Unit> itemStackMainHandHandler = MethodHandleHelper.wrapperForSetter(ItemRenderer.class, "d", "field_187467_d", "itemStackMainHand");
	private static Function2<ItemRenderer, Object, Unit> itemStackOffHandHandler = MethodHandleHelper.wrapperForSetter(ItemRenderer.class, "e", "field_187468_e", "itemStackOffHand");

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		LightningRenderer.INSTANCE.getClass();
		MinecraftForge.EVENT_BUS.register(new HudEventHandler());

		new WizardryClientMethodHandles(); // Load the class

		new Shaders();

		ModBlocks.initModel();
		ModEntities.initModels();

		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal_ring"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal_ring_outer"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/pearl"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_orb"));
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		CapeHandler.INSTANCE.getClass(); // ...
		OBJLoader.INSTANCE.addDomain(Wizardry.MODID);

		Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		RenderPlayer render = skinMap.get("default");
		render.addLayer(new BloodRenderLayer(render));

		render = skinMap.get("slim");
		render.addLayer(new BloodRenderLayer(render));

		if (ConfigValues.versionChecker)
			VersionChecker.init();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	@Override
	public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		MinecraftForge.EVENT_BUS.post(new ResourceReloadEvent(resourceManager));
	}

	@Override
	public void setItemStackHandHandler(EnumHand hand, ItemStack stack) {
		if (hand == EnumHand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getMinecraft().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getMinecraft().getItemRenderer(), stack);
	}

	public static class ResourceReloadEvent extends Event {
		public final IResourceManager resourceManager;

		public ResourceReloadEvent(IResourceManager manager) {
			resourceManager = manager;
		}
	}
}
