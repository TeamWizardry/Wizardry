package com.teamwizardry.wizardry.api.capability.world;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public final class WizardryWorldCapability {
	private static final ResourceLocation WIZARDRY_WORLD_ID = new ResourceLocation(Wizardry.MODID, "wizardry_world");
	@CapabilityInject(WizardryWorld.class)
	private static final Capability<WizardryWorld> CAPABILITY = null;

	private WizardryWorldCapability() {
	}

	public static Capability<WizardryWorld> capability() {
		//noinspection ConstantConditions
		return Objects.requireNonNull(CAPABILITY, "MANA_CAPABILITY");
	}

	// call in preinit
	public static void init() {
		CapabilityManager.INSTANCE.register(WizardryWorld.class, new WizardryWorldStorage(), StandardWizardryWorld::create);
	}

	public static WizardryWorld get(World world) {
		WizardryWorld cap = world.getCapability(capability(), null);
		if (cap == null) {
			throw new IllegalStateException("Missing capability: " + world.getWorldInfo().getWorldName() + "/" + world.provider.getDimensionType().getName());
		}
		return cap;
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<World> event) {
		event.addCapability(WIZARDRY_WORLD_ID, StandardWizardryWorld.create(event.getObject()));
	}

	// other event subscriptions related to cap behavior

	private static final class WizardryWorldStorage implements Capability.IStorage<WizardryWorld> {
		@Override
		public NBTBase writeNBT(Capability<WizardryWorld> capability, WizardryWorld world, EnumFacing side) {
			return world.serializeNBT();
		}

		@Override
		public void readNBT(Capability<WizardryWorld> capability, WizardryWorld world, EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound) {
				world.deserializeNBT((NBTTagCompound) nbt);
			}
		}
	}
}
