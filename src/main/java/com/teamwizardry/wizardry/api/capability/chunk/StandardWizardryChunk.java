package com.teamwizardry.wizardry.api.capability.chunk;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.teamwizardry.wizardry.api.block.TileCachable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class StandardWizardryChunk implements WizardryChunk
{
	private Set<TileCachable> cachedTiles = Collections.newSetFromMap(new WeakHashMap<TileCachable, Boolean>());
	
	public static StandardWizardryChunk create()
	{
		return new StandardWizardryChunk();
	}
	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == WizardryChunkCapability.capability();
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == WizardryChunkCapability.capability() ? WizardryChunkCapability.capability().cast(this) : null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){}

	@Override
	public void addCachableTile(TileCachable tile)
	{
		cachedTiles.add(tile);
	}

	@Override
	public Set<TileCachable> getCachableTiles()
	{
		return cachedTiles;
	}

}
