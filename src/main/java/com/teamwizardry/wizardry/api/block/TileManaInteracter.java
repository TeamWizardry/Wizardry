package com.teamwizardry.wizardry.api.block;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

import static net.minecraft.util.EnumFacing.*;

public class TileManaInteracter extends TileMod implements ITickable, IManaInteractable {

	static Cache<BlockPos, Object> MANA_INTERACTABLES = CacheBuilder
			.newBuilder()
			.expireAfterAccess(1, TimeUnit.MINUTES)
			.build();

	@Save
	@CapabilityProvide(sides = {DOWN, UP, NORTH, SOUTH, WEST, EAST})
	public CustomWizardryCapability cap;

	public TileManaInteracter(double maxMana, double maxBurnout) {
		cap = new CustomWizardryCapability(maxMana, maxBurnout);
	}

	@Nullable
	@Override
	public IWizardryCapability getCap() {
		return cap;
	}

	@Override
	public void update() {
		if (world.isRemote) return;
		if (!MANA_INTERACTABLES.asMap().containsKey(getPos())) MANA_INTERACTABLES.put(getPos(), 0);
	}
}
