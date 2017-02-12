package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockModVariant;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class BlockWisdomWoodPlanks extends BlockModVariant {

	public BlockWisdomWoodPlanks() {
		super("wisdom_wood_planks", Material.WOOD, "wisdom_wood_planks", "wisdom_wood_planks_pigmented");
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
