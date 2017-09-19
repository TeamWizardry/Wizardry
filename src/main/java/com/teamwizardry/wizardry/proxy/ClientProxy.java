package com.teamwizardry.wizardry.proxy;

import javax.annotation.Nonnull;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.utilities.client.CustomBlockMapSprites;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
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
}
