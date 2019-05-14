package com.teamwizardry.wizardry.proxy;

import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.utilities.client.CustomBlockMapSprites;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.client.core.renderer.PearlRadialUIRenderer;
import com.teamwizardry.wizardry.client.cosmetics.CapeHandler;
import com.teamwizardry.wizardry.client.cosmetics.CosmeticsManager;
import com.teamwizardry.wizardry.client.render.item.RenderHaloEntity;
import com.teamwizardry.wizardry.common.core.version.VersionChecker;
import com.teamwizardry.wizardry.init.ModEntities;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModKeybinds;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

//import com.teamwizardry.wizardry.client.core.renderer.PearlRadialUIRenderer;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	// Trigger hand swinging when on staff cooldowns
	private static Function2<ItemRenderer, Object, Unit> itemStackMainHandHandler = MethodHandleHelper.wrapperForSetter(ItemRenderer.class, "d", "field_187467_d", "itemStackMainHand");
	private static Function2<ItemRenderer, Object, Unit> itemStackOffHandHandler = MethodHandleHelper.wrapperForSetter(ItemRenderer.class, "e", "field_187468_e", "itemStackOffHand");

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		if (ConfigValues.versionCheckerEnabled)
			VersionChecker.register();

		ModEntities.initModels();

		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal_ring"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal_ring_outer"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_crystal"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/outputPearl"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_orb"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/mana_pearl_cube"));
		CustomBlockMapSprites.INSTANCE.register(new ResourceLocation(Wizardry.MODID, "blocks/nacre_pearl_cube"));

		// Load and bake the 2D models
		ModelBakery.registerItemVariants(ModItems.BOOK, new ModelResourceLocation("wizardry:book", "inventory"));
		ModelResourceLocation default3dPath = new ModelResourceLocation("wizardry:book", "inventory");
		ModelLoader.setCustomMeshDefinition(ModItems.BOOK, stack -> default3dPath);

		ModKeybinds.register();

		PearlRadialUIRenderer.INSTANCE.getClass();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		MinecraftForge.EVENT_BUS.register(CapeHandler.instance());

		Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
		for (RenderPlayer render : skinMap.values()) {
			//	render.addLayer(new BloodRenderLayer(render));
			render.addLayer(new RenderHaloEntity(render.getMainModel().bipedHead));
		}

		Map<Class<? extends Entity>, Render<? extends Entity>> map = Minecraft.getMinecraft().getRenderManager().entityRenderMap;
		for (ResourceLocation entity : EntityList.getEntityNameList()) {
			Class<? extends Entity> clazz = EntityList.getClass(entity);
			Render<? extends Entity> entityRenderer = map.get(clazz);
			if (entityRenderer instanceof RenderLiving<?>) {
				ModelBase main = ((RenderLiving<?>) entityRenderer).getMainModel();
				ModelRenderer renderer = null;
				if (main instanceof ModelBiped) renderer = ((ModelBiped) main).bipedHead;

				((RenderLiving<?>) entityRenderer).addLayer(new RenderHaloEntity(renderer));
			}
		}

		new CosmeticsManager().initClient();
	}

	@Override
	public void setItemStackHandHandler(EnumHand hand, ItemStack stack) {
		if (hand == EnumHand.MAIN_HAND)
			itemStackMainHandHandler.invoke(Minecraft.getMinecraft().getItemRenderer(), stack);
		else itemStackOffHandHandler.invoke(Minecraft.getMinecraft().getItemRenderer(), stack);
	}
}
