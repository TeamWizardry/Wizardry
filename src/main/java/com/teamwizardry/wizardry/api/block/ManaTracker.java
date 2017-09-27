package com.teamwizardry.wizardry.api.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import com.teamwizardry.wizardry.init.ModBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ManaTracker
{
	public static ManaTracker INSTANCE = new ManaTracker();
	private LinkedList<ManaBluetooth> blueteeth = new LinkedList<>();
	private HashMap<World, HashSet<BlockPos>> trackedLocs = new HashMap<>();
	
	private void trackPos(World world, BlockPos pos)
	{
		trackedLocs.putIfAbsent(world, new HashSet<>());
		trackedLocs.get(world).add(pos);
	}
	
	private boolean isPosTracked(World world, BlockPos pos)
	{
		return trackedLocs.getOrDefault(world, new HashSet<>()).contains(pos);
	}
	
	private void untrackPos(World world, BlockPos pos)
	{
		trackedLocs.getOrDefault(world, new HashSet<>()).remove(pos);
	}
	
	public void addManaCraft(World world, BlockPos pos, BiPredicate<World, BlockPos> input, int duration, BiConsumer<World, BlockPos> output)
	{
		if (isPosTracked(world, pos))
			return;
		if (world.getBlockState(pos).getBlock() == ModBlocks.FLUID_MANA)
		{
			blueteeth.add(new ManaBluetooth(world, pos, input, duration, output));
			trackPos(world, pos);
		}
	}
	
	public void addManaCraft(World world, BlockPos pos, BiPredicate<World, BlockPos> input, int duration, QuadConsumer<World, BlockPos, Integer, Integer> tickEffect, BiConsumer<World, BlockPos> output)
	{
		if (isPosTracked(world, pos))
			return;
		if (world.getBlockState(pos).getBlock() == ModBlocks.FLUID_MANA)
		{
			blueteeth.add(new ManaBluetooth(world, pos, input, duration, tickEffect, output));
			trackPos(world, pos);
		}
	}
	
	public void tick()
	{
		if (blueteeth.isEmpty())
			return;
		LinkedList<ManaBluetooth> temp = new LinkedList<>(blueteeth);
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Mana Tracker: " + temp.size() + " tracked effects"));
		temp.forEach(bluetooth -> {
			bluetooth.tick();
			if (bluetooth.isExpired)
			{
				blueteeth.remove(bluetooth);
				untrackPos(bluetooth.world, bluetooth.position);
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Mana Tracker: Stopped tracking at " + bluetooth.position));
			}
		});
	}
	
	public class ManaBluetooth
	{
		private World world;
		private BlockPos position;
		private BiPredicate<World, BlockPos> input;
		private int duration;
		private QuadConsumer<World, BlockPos, Integer, Integer> tickEffect;
		private BiConsumer<World, BlockPos> output;
		
		private int cookTime = 0;
		public boolean isExpired = false;
		
		ManaBluetooth(World world, BlockPos pos, BiPredicate<World, BlockPos> input, int duration, BiConsumer<World, BlockPos> output)
		{
			this(world, pos, input, duration, (worldIn, posIn, cookTimeIn, durationIn) -> {}, output);
		}
		
		ManaBluetooth(World world, BlockPos pos, BiPredicate<World, BlockPos> input, int duration, QuadConsumer<World, BlockPos, Integer, Integer> tickEffect, BiConsumer<World, BlockPos> output)
		{
			this.world = world;
			this.position = pos;
			this.input = input;
			this.duration = duration;
			this.tickEffect = tickEffect;
			this.output = output;
		}
		
		public void tick()
		{
			if (world.getBlockState(position).getBlock() != ModBlocks.FLUID_MANA)
			{
				isExpired = true;
				return;
			}
			if (!input.test(world, position))
			{
				isExpired = true;
				return;
			}
			if (cookTime >= duration)
			{
				output.accept(world, position);
				isExpired = true;
				return;
			}
			cookTime++;
			tickEffect.accept(world, position, cookTime, duration);
		}
	}
	
	@FunctionalInterface
	public interface QuadConsumer<T, U, V, W>
	{
		public void accept(T t, U u, V v, W w);
	}
}
