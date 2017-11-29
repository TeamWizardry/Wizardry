package com.teamwizardry.wizardry.init;


import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.block.*;
import com.teamwizardry.wizardry.common.block.fluid.BlockFluidMana;
import com.teamwizardry.wizardry.common.block.fluid.BlockFluidNacre;
import com.teamwizardry.wizardry.common.block.fluid.FluidMana;
import com.teamwizardry.wizardry.common.block.fluid.FluidNacre;
import com.teamwizardry.wizardry.common.block.wisdomwood.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Saad on 3/24/2016.
 */
@Mod.EventBusSubscriber
public class ModBlocks {

	//public static Material NACRE_MATERIAL = new MaterialNacre(MapColor.WATER);
	//public static Material MANA_MATERIAL = new MaterialMana(MapColor.WATER);

	public static BlockFluidMana FLUID_MANA;
	public static BlockFluidNacre FLUID_NACRE;

	public static BlockCraftingPlate CRAFTING_PLATE;
	public static BlockMagiciansWorktable MAGICIANS_WORKTABLE;
	public static BlockManaBattery MANA_BATTERY;
	public static BlockCreativeManaBattery CREATIVE_MANA_BATTERY;
	public static BlockPearlHolder PEARL_HOLDER;
	public static BlockCloud CLOUD;
	public static BlockManaMagnet MANA_MAGNET;
	public static BlockLight LIGHT;
	public static BlockJar JAR;

	public static BlockNacre NACRE;
	public static BlockNacreBrick NACRE_BRICK;

	public static BlockWisdomLeaves WISDOM_LEAVES;
	public static BlockWisdomWoodLog WISDOM_WOOD_LOG;
	public static BlockWisdomWoodPlanks WISDOM_WOOD_PLANKS;
	public static BlockWisdomWoodSlab WISDOM_WOOD_SLAB;
	public static BlockWisdomWoodStairs WISDOM_WOOD_STAIRS;
	public static BlockWisdomWoodTrapdoor WISDOM_WOOD_TRAPDOOR;
	public static BlockWisdomWoodDoor WISDOM_WOOD_DOOR;
	public static BlockWisdomWoodFence WISDOM_WOOD_FENCE;
	public static BlockWisdomWoodFenceGate WISDOM_WOOD_FENCE_GATE;
	public static BlockWisdomWoodPigmentedPlanks WISDOM_WOOD_PIGMENTED_PLANKS;

	public static BlockTorikkiGrass TORIKKI_GRASS;

	public static BlockUnicornTrail UNICORN_TRAIL;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> evt) {
		IForgeRegistry<Block> r = evt.getRegistry();

		r.register(FLUID_MANA = new BlockFluidMana());
		r.register(FLUID_NACRE = new BlockFluidNacre());
	}

	public static void init() {

		CRAFTING_PLATE = new BlockCraftingPlate();
		MAGICIANS_WORKTABLE = new BlockMagiciansWorktable();
		MANA_BATTERY = new BlockManaBattery();
		CREATIVE_MANA_BATTERY = new BlockCreativeManaBattery();
		PEARL_HOLDER = new BlockPearlHolder();
		CLOUD = new BlockCloud();
		NACRE = new BlockNacre();
		NACRE_BRICK = new BlockNacreBrick();
		MANA_MAGNET = new BlockManaMagnet();
		LIGHT = new BlockLight();
		JAR = new BlockJar();

		WISDOM_LEAVES = new BlockWisdomLeaves();
		WISDOM_WOOD_LOG = new BlockWisdomWoodLog();
		WISDOM_WOOD_PLANKS = new BlockWisdomWoodPlanks();
		WISDOM_WOOD_SLAB = new BlockWisdomWoodSlab();
		WISDOM_WOOD_STAIRS = new BlockWisdomWoodStairs();
		WISDOM_WOOD_TRAPDOOR = new BlockWisdomWoodTrapdoor();
		WISDOM_WOOD_DOOR = new BlockWisdomWoodDoor();
		WISDOM_WOOD_FENCE = new BlockWisdomWoodFence();
		WISDOM_WOOD_FENCE_GATE = new BlockWisdomWoodFenceGate();
		WISDOM_WOOD_PIGMENTED_PLANKS = new BlockWisdomWoodPigmentedPlanks();

		TORIKKI_GRASS = new BlockTorikkiGrass();

		UNICORN_TRAIL = new BlockUnicornTrail();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event) {
		CRAFTING_PLATE.initModel();
		MANA_BATTERY.initModel();
		//CREATIVE_MANA_BATTERY.initModel();
		PEARL_HOLDER.initModel();
		JAR.initModel();
		MANA_MAGNET.initModel();
		registerFluidRender(FluidMana.instance);
		registerFluidRender(FluidNacre.instance);
	}

	@SideOnly(Side.CLIENT)
	private void registerFluidRender(Fluid f) {
		FluidCustomModelMapper mapper = new FluidCustomModelMapper(f);
		Block block = f.getBlock();
		if (block != null) {
			Item item = Item.getItemFromBlock(block);
			if (item != Items.AIR) {
				ModelLoader.registerItemVariants(item);
				ModelLoader.setCustomMeshDefinition(item, mapper);
			} else {
				ModelLoader.setCustomStateMapper(block, mapper);
			}
		}
	}

	public static class FluidCustomModelMapper extends StateMapperBase implements ItemMeshDefinition {

		private final ModelResourceLocation res;

		public FluidCustomModelMapper(Fluid f) {
			this.res = new ModelResourceLocation(Wizardry.MODID + ":blockfluids", f.getName());
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			return res;
		}

		@Override
		public ModelResourceLocation getModelResourceLocation(IBlockState state) {
			return res;
		}

	}
}
