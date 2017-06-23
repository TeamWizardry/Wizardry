package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.block.TileManaSink;
import com.teamwizardry.wizardry.api.spell.IContinuousSpell;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TilePearlHolder extends TileManaSink {

	@Save
	public ItemStack pearl = ItemStack.EMPTY;
	@Save
	private int cooldown = 0;

	public TilePearlHolder() {
		super(10000, 10000);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) return;
		if (pearl == null) pearl = ItemStack.EMPTY;

		if (pearl.getItem() == ModItems.PEARL_NACRE) {

			if (cooldown <= 0) {
				int maxCooldown = 0;
				for (Module module : SpellStack.getAllModules(pearl)) {
					if (module instanceof IContinuousSpell) return;
					if (module.getCooldownTime() > maxCooldown) maxCooldown = module.getCooldownTime();
				}
				cooldown = maxCooldown;
			} else {
				cooldown--;
				return;
			}
			for (int i = -6; i < 6; i++)
				for (int j = -6; j < 6; j++)
					for (int k = -6; k < 6; k++) {
						BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY() + j, getPos().getZ() + k);
						if (world.getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;

						Vec3d direction = new Vec3d(getPos()).subtract(new Vec3d(pos)).normalize();
						float[] rotations = PosUtils.vecToRotations(direction);

						SpellData spell = new SpellData(getWorld());
						spell.addData(YAW, rotations[1]);
						spell.addData(PITCH, rotations[0]);
						spell.addData(ORIGIN, new Vec3d(getPos()).addVector(0.5, 2.5, 0.5));
						spell.addData(CAPABILITY, cap);
						SpellStack.runSpell(pearl, spell);
						break;
					}
		}
	}
}
