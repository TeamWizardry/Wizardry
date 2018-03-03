package com.teamwizardry.wizardry.api.block;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.teamwizardry.wizardry.crafting.mana.FluidCraftInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FluidTracker {
	public static FluidTracker INSTANCE = new FluidTracker();
	private HashMap<Integer, HashMultimap<BlockPos, FluidCraftInstance>> fluidCrafters = new HashMap<>();

	public void addManaCraft(World world, BlockPos pos, FluidCraftInstance crafter) {
		int dim = world.provider.getDimension();
		HashMultimap<BlockPos, FluidCraftInstance> worldCrafters = fluidCrafters.get(dim);
		if (worldCrafters == null) {
			worldCrafters = HashMultimap.create();
			fluidCrafters.put(dim, worldCrafters);
		}
		Set<FluidCraftInstance> crafterList = worldCrafters.get(pos);
		for (FluidCraftInstance manaCrafter : crafterList)
			if (manaCrafter.equals(crafter))
				return;
		if (world.getBlockState(pos).getBlock() == crafter.getFluid().getBlock())
			worldCrafters.put(pos, crafter);
	}

	public void tick(World tickedWorld) {
		if (fluidCrafters.isEmpty())
			return;
		int dim = tickedWorld.provider.getDimension();
		HashMultimap<BlockPos, FluidCraftInstance> worldCrafters = fluidCrafters.get(dim);
		if (worldCrafters == null || worldCrafters.isEmpty())
			return;
		HashMultimap<BlockPos, FluidCraftInstance> crafterToRemove = HashMultimap.create();
		Multimaps.asMap(worldCrafters).forEach((pos, crafterList) -> {
			if (!tickedWorld.isBlockLoaded(pos))
				return;
			List<EntityItem> items = tickedWorld.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(1));
			crafterList.forEach(crafter -> {
				if (!crafter.isValid(tickedWorld, pos, items)) {
					crafterToRemove.put(pos, crafter);
				} else {
					crafter.tick(tickedWorld, pos, items);
					if (crafter.isFinished()) {
						crafter.finish(tickedWorld, pos, items);
						crafterToRemove.put(pos, crafter);
					}
				}
			});
		});

		crafterToRemove.forEach(worldCrafters::remove);
		if (worldCrafters.isEmpty())
			fluidCrafters.remove(dim);
	}
}
