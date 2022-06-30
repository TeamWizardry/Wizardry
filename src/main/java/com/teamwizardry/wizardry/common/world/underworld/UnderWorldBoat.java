package com.teamwizardry.wizardry.common.world.underworld;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Map;
import java.util.Random;

public class UnderWorldBoat extends WorldGenerator {

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		MinecraftServer minecraftServer = world.getMinecraftServer();
		TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
		Template underworldBoat = templateManager.getTemplate(minecraftServer, new ResourceLocation(Wizardry.MODID, "underworld_boat"));

		PlacementSettings placementSettings = new PlacementSettings();
		Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
		placementSettings.setIgnoreEntities(false).setMirror(Mirror.NONE).setRotation(rotation).setIgnoreStructureBlock(false);

		underworldBoat.addBlocksToWorld(world, position, placementSettings);

		// Get the data blocks to add custom loot to the boat
		Map<BlockPos, String> dataBlocks = underworldBoat.getDataBlocks(position, placementSettings);
		for (Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			if (entry.getValue().equals("lootchest")) {
				BlockPos blockPos = entry.getKey();
				world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
				TileEntity tileEntity = world.getTileEntity(blockPos.down());

				if (tileEntity instanceof TileEntityChest) {
					((TileEntityChest) tileEntity).setLootTable(CommonProxy.UNDERWORLD_BOAT_CHEST, rand.nextLong());
				}
			}
		}


		return true;
	}

}
