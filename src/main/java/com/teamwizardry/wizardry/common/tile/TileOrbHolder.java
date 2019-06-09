package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.TileManaNode;
import com.teamwizardry.wizardry.api.capability.mana.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.mana.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.ICooldownSpellCaster;
import com.teamwizardry.wizardry.api.item.IManaCell;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 5/7/2016.
 */
@TileRegister(Wizardry.MODID + ":pedestal")
public class TileOrbHolder extends TileManaNode implements ICooldownSpellCaster {

	@Module
	public ModuleInventory inventory = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			if (stack.getItem() instanceof IManaCell) {
				//Max number of orbs the orb holder can hold
				return 1;
			} else {
				return 0;
			}
		}
	});

	public TileOrbHolder() {
		super(300, 300);
	}

	@Nullable
	@Override
	public IWizardryCapability getWizardryCap() {
		if (containsCell()) {
			return WizardryCapabilityProvider.getCap(getItemStack());
		}
		return null;
	}

	@Nonnull
	@Override
	public Vec3d getOffset() {
		return new Vec3d(0, 0.5, 0);
	}

	@Override
	public void update() {
		super.update();
		if (containsCell()) {
			ModItems.ORB.onUpdate(getItemStack(), getWorld(), null, 0, true);
		}
	}

	public boolean containsCell() {
		return getItemStack().getItem() instanceof IManaCell;
	}

	public ItemStack getItemStack() {
		return inventory.getHandler().getStackInSlot(0);
	}

	public void setItemStack(ItemStack stack) {
		inventory.getHandler().setStackInSlot(0, stack);
	}
}
