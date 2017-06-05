package com.teamwizardry.wizardry.common.block;

import com.teamwizardry.librarianlib.features.base.block.BlockMod;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * Created by Saad on 9/4/2016.
 */
public class BlockManaMagnet extends BlockMod {

	public BlockManaMagnet() {
		super("mana_magnet", Material.IRON);
		setHardness(0.7f);
		setSoundType(SoundType.METAL);
	}
}
