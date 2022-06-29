package com.teamwizardry.wizardry.common.world.underworld;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class UnderWorldBoat extends WorldGenerator {

	@Override
	public boolean generate(World world, Random rand, BlockPos position) {
		MinecraftServer minecraftserver = world.getMinecraftServer();
		TemplateManager templatemanager = world.getSaveHandler().getStructureTemplateManager();
		Template underworld_boat = templatemanager.getTemplate(minecraftserver, new ResourceLocation(Wizardry.MODID, "underworld_boat"));

		PlacementSettings placementsettings = new PlacementSettings();
		Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
		placementsettings.setIgnoreEntities(false).setMirror(Mirror.NONE).setRotation(rotation);
		underworld_boat.addBlocksToWorld(world, position, placementsettings);

		return true;
	}

}
