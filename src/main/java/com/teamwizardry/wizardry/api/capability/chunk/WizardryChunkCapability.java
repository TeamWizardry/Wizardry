package com.teamwizardry.wizardry.api.capability.chunk;

import java.util.Objects;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public final class WizardryChunkCapability
{
	private static final ResourceLocation WIZARDRY_CHUNK_ID = new ResourceLocation(Wizardry.MODID, "wizardry_chunk");
	@CapabilityInject(WizardryChunk.class)
	private static final Capability<WizardryChunk> CAPABILITY = null;
	
	private WizardryChunkCapability(){}
	
	public static Capability<WizardryChunk> capability()
	{
		return Objects.requireNonNull(CAPABILITY, "CAPABILITY");
	}
	
	public static void init()
	{
		CapabilityManager.INSTANCE.register(WizardryChunk.class, new WizardryChunkStorage(), StandardWizardryChunk::create);
	}
	
	public static WizardryChunk get(Chunk chunk)
	{
		WizardryChunk cap = chunk.getCapability(capability(), null);
		if (cap == null)
			throw new IllegalStateException("Missing capability: " + chunk.getWorld().getWorldInfo().getWorldName() + "/" + chunk.getWorld().provider.getDimensionType().getName() + "/(" + chunk.x + ","+ chunk.z + ")");
		return cap;
	}
	
	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Chunk> event)
	{
		event.addCapability(WIZARDRY_CHUNK_ID, StandardWizardryChunk.create(event.getObject()));
	}
	
	private static final class WizardryChunkStorage implements Capability.IStorage<WizardryChunk>
	{
		@Override
		public NBTBase writeNBT(Capability<WizardryChunk> capability, WizardryChunk chunk, EnumFacing side)
		{
			return chunk.serializeNBT();
		}

		@Override
		public void readNBT(Capability<WizardryChunk> capability, WizardryChunk chunk, EnumFacing side, NBTBase nbt)
		{
			if (nbt instanceof NBTTagCompound)
				chunk.deserializeNBT((NBTTagCompound) nbt);
		}
	}
}
