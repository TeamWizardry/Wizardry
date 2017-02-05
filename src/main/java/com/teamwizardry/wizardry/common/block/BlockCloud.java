package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 8/27/2016.
 */
public class BlockCloud extends BlockMod {

	public BlockCloud() {
		super("cloud", Material.CLOTH);
		setHardness(0.5f);
		//setLightLevel(10);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
