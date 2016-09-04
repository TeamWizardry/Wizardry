package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.common.base.ModCreativeTab;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Saad on 9/4/2016.
 */
public class BlockManaMagnet extends BlockMod {

	public BlockManaMagnet() {
		super("mana_magnet", Material.IRON);
		setHardness(0.7f);
		setSoundType(SoundType.METAL);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
