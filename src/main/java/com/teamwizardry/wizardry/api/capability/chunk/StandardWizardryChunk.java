package com.teamwizardry.wizardry.api.capability.chunk;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import com.teamwizardry.wizardry.api.block.TileCachable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

public class StandardWizardryChunk implements WizardryChunk
{
	private Chunk chunk;
	private Set<TileEntity> cachedTiles = Collections.newSetFromMap(new WeakHashMap<TileEntity, Boolean>());
	
	public static StandardWizardryChunk create(Chunk chunk)
	{
		StandardWizardryChunk wizardryChunk = new StandardWizardryChunk();
		wizardryChunk.chunk = chunk;
		return wizardryChunk;
	}
	
	public static StandardWizardryChunk create()
	{
		return new StandardWizardryChunk();
	}
	
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCachableTile(TileCachable tile)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<TileCachable> getCachableTiles()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
