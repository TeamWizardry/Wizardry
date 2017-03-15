package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.spell.IContinousSpell;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.entity.EntityStaffFakePlayer;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TileStaff extends TileMod implements ITickable {

	@Nullable
	@Save
	public ItemStack pearl;
	private EntityStaffFakePlayer fakePlayer = null;
	@Save
	private int cooldown = 0;

	@Override
	public void update() {
		if (world.isRemote) return;
		if (pearl == null) return;
		if (pearl.getItem() == ModItems.PEARL_NACRE) {

			if (cooldown <= 0) {
				int itemHoldDown = 0;
				boolean flag = false;
				for (Module module : SpellStack.getAllModules(pearl))
					if (!(module instanceof IContinousSpell))
						itemHoldDown += module.getChargeUpTime();
					else flag = true;

				if (itemHoldDown == 0 && !flag) cooldown = 10;
				else cooldown = itemHoldDown;
			} else {
				cooldown--;
				return;
			}
			for (int i = -4; i < 4; i++)
				for (int j = -4; j < 4; j++)
					for (int k = -4; k < 4; k++) {
						BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY() + j, getPos().getZ() + k);
						if (world.getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;

						Vec3d direction = new Vec3d(getPos()).addVector(0.5, 0.5, 0.5).subtract(new Vec3d(pos).addVector(0.5, 0.5, 0.5)).normalize();
						float[] rotations = PosUtils.vecToRotations(direction);

						SpellData spell = new SpellData(getWorld());
						spell.addData(YAW, rotations[1]);
						spell.addData(PITCH, rotations[0]);
						spell.addData(ORIGIN, new Vec3d(getPos()).addVector(0.5, 2.5, 0.5));
						SpellStack.runModules(pearl, spell);
						break;
					}
		}
	}
}
