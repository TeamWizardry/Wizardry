package com.teamwizardry.wizardry.init;

import com.teamwizardry.librarianlib.common.structure.Structure;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public enum ModStructures {
	INSTANCE;

	public static Structure craftingAltar;

	ModStructures() {
		reload();
	}

	public static void reload() {
		craftingAltar = new Structure(new ResourceLocation(Wizardry.MODID, "crafting_altar"));
	}

	public static boolean matchTemplateToPos(Template template, World world, BlockPos origin) {
		if (!template.blocks.isEmpty() && (template.getSize().getX() >= 1) && (template.getSize().getY() >= 1) && (template.getSize().getZ() >= 1)) {

			List<BlockInfo> blocks = template.blocks.stream().filter(info -> info.blockState.getBlock() != Blocks.AIR).collect(Collectors.toList());

			for (Iterator<BlockInfo> iterator = blocks.iterator(); iterator.hasNext(); ) {
				BlockInfo blockInfo = iterator.next();
				BlockPos blockpos = blockInfo.pos.add(origin);
				IBlockState state = world.getBlockState(blockpos);
				if ((state == blockInfo.blockState)
						|| (state.getActualState(world, blockpos) == blockInfo.blockState.getActualState(world, blockpos))) {
					iterator.remove();

				}
			}
			if (!blocks.isEmpty()) {
				LibParticles.TEMPLATE_BLOCK_ERROR(world, new Vec3d(blocks.get(0).pos.add(origin)).addVector(0.5, 0.5, 0.5));
				return false;
			}
		}
		return true;
	}
}

