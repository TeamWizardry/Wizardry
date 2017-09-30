package com.teamwizardry.wizardry.api.block;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.teamwizardry.librarianlib.features.utilities.DimWithPos;
import com.teamwizardry.wizardry.crafting.mana.ManaCrafter;
import com.teamwizardry.wizardry.init.ModBlocks;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class ManaTracker
{
	public static ManaTracker INSTANCE = new ManaTracker();
	private HashMultimap<DimWithPos, ManaCrafter> manaCrafters = HashMultimap.create();
	
	public void addManaCraft(World world, BlockPos pos, ManaCrafter crafter)
	{
		DimWithPos key = new DimWithPos(world, pos);
		Set<ManaCrafter> crafterList = manaCrafters.get(key);
		for (ManaCrafter manaCrafter : crafterList)
			if (manaCrafter.equals(crafter))
				return;
		if (world.getBlockState(pos) == ModBlocks.FLUID_MANA.getDefaultState())
			manaCrafters.put(key, crafter);
	}
	
	public void tick()
	{
		if (manaCrafters.isEmpty())
			return;
		LinkedList<DimWithPos> posToRemove = new LinkedList<>();
		HashMultimap<DimWithPos, ManaCrafter> crafterToRemove = HashMultimap.create();
		Multimaps.asMap(manaCrafters).forEach((dimPos, crafterList) -> {
			World world = DimensionManager.getWorld(dimPos.getDim());
			BlockPos pos = dimPos.getPos();
			if (!world.isBlockLoaded(pos))
				return;
			if (world.getBlockState(pos).getBlock() != ModBlocks.FLUID_MANA)
			{
				posToRemove.add(dimPos);
				return;
			}
			List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos));
			crafterList.forEach(crafter -> {
				if (!crafter.isValid(world, pos, items))
				{
					crafterToRemove.put(dimPos, crafter);
					return;
				}
				crafter.tick(world, pos, items);
				if (crafter.isFinished())
				{
					crafter.finish(world, pos, items);
					crafterToRemove.put(dimPos, crafter);
					return;
				}
			});
		});
		
		posToRemove.forEach(pos -> manaCrafters.removeAll(pos));
		crafterToRemove.forEach((pos, crafter) -> manaCrafters.remove(pos, crafter));
	}
}
