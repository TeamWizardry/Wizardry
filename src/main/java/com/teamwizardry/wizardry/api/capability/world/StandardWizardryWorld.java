package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.IDelayedModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.common.core.SpellTicker;
import com.teamwizardry.wizardry.common.network.PacketSyncWizardryWorld;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;

public class StandardWizardryWorld implements WizardryWorld {

	private World world;
	private HashSet<SpellTicker.LingeringObject> lingeringStorageSet = new HashSet<>();
	private HashSet<SpellTicker.DelayedObject> delayedStorageSet = new HashSet<>();


	public static StandardWizardryWorld create(World world) {
		StandardWizardryWorld wizardryWorld = new StandardWizardryWorld();
		wizardryWorld.world = world;
		return wizardryWorld;
	}

	public static StandardWizardryWorld create() {
		return new StandardWizardryWorld();
	}

	@Override
	public void addLingerSpell(SpellRing spellRing, SpellData data, int expiry) {
		lingeringStorageSet.add(new SpellTicker.LingeringObject(spellRing, data, data.world.getTotalWorldTime(), expiry));

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), data.world.provider.getDimension());
	}

	@Override
	public void addDelayedSpell(Module module, SpellRing spellRing, SpellData data, int expiry) {
		if (module instanceof IDelayedModule)
			delayedStorageSet.add(new SpellTicker.DelayedObject(module, spellRing, data, data.world.getTotalWorldTime(), expiry));

		PacketHandler.NETWORK.sendToDimension(new PacketSyncWizardryWorld(serializeNBT()), data.world.provider.getDimension());
	}

	@Override
	public HashSet<SpellTicker.LingeringObject> getLingeringObjects() {
		return lingeringStorageSet;
	}

	@Override
	public HashSet<SpellTicker.DelayedObject> getDelayedObjects() {
		return delayedStorageSet;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();

		NBTTagList lingeringNBT = new NBTTagList();
		for (SpellTicker.LingeringObject object : lingeringStorageSet) {
			lingeringNBT.appendTag(object.serializeNBT());
		}

		NBTTagList delayedNBT = new NBTTagList();
		for (SpellTicker.DelayedObject object : delayedStorageSet) {
			delayedNBT.appendTag(object.serializeNBT());
		}

		compound.setTag("lingering", lingeringNBT);
		compound.setTag("delayed", delayedNBT);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		if (compound.hasKey("lingering")) {
			NBTTagList lingeringNBT = compound.getTagList("lingering", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : lingeringNBT) {
				if (base instanceof NBTTagCompound) {
					lingeringStorageSet.add(SpellTicker.LingeringObject.deserialize(world, (NBTTagCompound) base));
				}
			}
		}

		if (compound.hasKey("delayed")) {
			NBTTagList delayedNBT = compound.getTagList("delayed", Constants.NBT.TAG_COMPOUND);
			for (NBTBase base : delayedNBT) {
				if (base instanceof NBTTagCompound) {
					delayedStorageSet.add(SpellTicker.DelayedObject.deserialize(world, (NBTTagCompound) base));
				}
			}
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == WizardryWorldCapability.capability();
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return capability == WizardryWorldCapability.capability() ? WizardryWorldCapability.capability().cast(this) : null;
	}
}
