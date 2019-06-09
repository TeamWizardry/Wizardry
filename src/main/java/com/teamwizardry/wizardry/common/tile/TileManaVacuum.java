package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleFluid;
import com.teamwizardry.librarianlib.features.base.block.tile.module.ModuleInventory;
import com.teamwizardry.librarianlib.features.saving.Module;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.TileManaNode;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

@TileRegister(Wizardry.MODID + ":mana_vacuum")
//@TileRenderer(TileManaVacuumRenderer.class)
public class TileManaVacuum extends TileManaNode
{
	private static final BlockPos pos = new BlockPos(0, 3, 0);
	
	public TileManaVacuum()
	{
		super(0, 0);
	}
	
	@Module
	public ModuleInventory devilDust = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack)
		{
			if (stack.getItem() == ModItems.DEVIL_DUST)
				return super.getStackLimit(slot, stack);
			return 0;
		}
	});

	@Module
	public ModuleInventory gunpowder = new ModuleInventory(new ItemStackHandler() {
		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack)
		{
			if (stack.getItem() == Items.GUNPOWDER)
				return super.getStackLimit(slot, stack);
			return 0;
		}
	});

	@Module
	public ModuleFluid mana = new ModuleFluid(ModFluids.MANA, 0, 1000);
	
	
}