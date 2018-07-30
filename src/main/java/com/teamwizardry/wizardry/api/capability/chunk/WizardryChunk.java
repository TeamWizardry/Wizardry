package com.teamwizardry.wizardry.api.capability.chunk;

import java.util.Set;

import com.teamwizardry.wizardry.api.block.TileCachable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface WizardryChunk extends ICapabilitySerializable<NBTTagCompound>
{
	void addCachableTile(TileCachable tile);
	
	Set<TileCachable> getCachableTiles();
}
