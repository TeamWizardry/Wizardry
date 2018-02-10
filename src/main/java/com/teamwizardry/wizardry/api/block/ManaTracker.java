package com.teamwizardry.wizardry.api.block;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.teamwizardry.wizardry.crafting.mana.ManaCrafter;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ManaTracker {
	public static ManaTracker INSTANCE = new ManaTracker();
	private HashMap<Integer, HashMultimap<BlockPos, ManaCrafter>> manaCrafters = new HashMap<>();

	public void addManaCraft(World world, BlockPos pos, ManaCrafter crafter) {
		int dim = world.provider.getDimension();
		HashMultimap<BlockPos, ManaCrafter> worldCrafters = manaCrafters.get(dim);
		if (worldCrafters == null) {
			worldCrafters = HashMultimap.create();
			manaCrafters.put(dim, worldCrafters);
		}
		Set<ManaCrafter> crafterList = worldCrafters.get(pos);
		for (ManaCrafter manaCrafter : crafterList)
			if (manaCrafter.equals(crafter))
				return;
		if (world.getBlockState(pos).getBlock() == ModBlocks.FLUID_MANA)
			worldCrafters.put(pos, crafter);
	}

	public void tick(World tickedWorld) {
		if (manaCrafters.isEmpty())
			return;
		int dim = tickedWorld.provider.getDimension();
		HashMultimap<BlockPos, ManaCrafter> worldCrafters = manaCrafters.get(dim);
		if (worldCrafters == null || worldCrafters.isEmpty())
			return;
		LinkedList<BlockPos> posToRemove = new LinkedList<>();
		HashMultimap<BlockPos, ManaCrafter> crafterToRemove = HashMultimap.create();
		Multimaps.asMap(worldCrafters).forEach((pos, crafterList) -> {
			if (!tickedWorld.isBlockLoaded(pos))
				return;
			if (tickedWorld.getBlockState(pos).getBlock() != ModBlocks.FLUID_MANA) {
				posToRemove.add(pos);
				return;
			}
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

		posToRemove.forEach(worldCrafters::removeAll);
		crafterToRemove.forEach(worldCrafters::remove);
		if (worldCrafters.isEmpty())
			manaCrafters.remove(dim);
	}
}
