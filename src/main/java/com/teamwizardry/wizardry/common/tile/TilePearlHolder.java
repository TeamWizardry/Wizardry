package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.block.TileManaSink;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TilePearlHolder extends TileManaSink implements ICooldown {

	@Save
	public ItemStack pearl = ItemStack.EMPTY;

	public TilePearlHolder() {
		super(10000, 10000);
	}

	@Nullable
	@Override
	public Predicate<TileManaSink> getSuckingCondition() {
		return tileManaSink -> pearl != null && !pearl.isEmpty() && pearl.getItem() == ModItems.PEARL_NACRE;
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) return;
		if (pearl == null) pearl = ItemStack.EMPTY;

		if (pearl.getItem() == ModItems.PEARL_NACRE) {
			updateCooldown(pearl);

			if (isCoolingDown(pearl)) return;

			BlockPos closestMagnet = null;
			for (int i = -10; i < 10; i++)
				for (int j = -10; j < 10; j++)
					for (int k = -10; k < 10; k++) {
						BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY() + j, getPos().getZ() + k);
						if (world.getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;
						if (closestMagnet == null) closestMagnet = pos;
						else if (pos.distanceSq(getPos()) < getPos().distanceSq(closestMagnet))
							closestMagnet = pos;
					}

			if (closestMagnet == null) return;
			{
				Vec3d direction = new Vec3d(closestMagnet)
						.addVector(0.5, 0, 0.5)
						.subtract(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5))
						.normalize();
				SpellData spell = new SpellData(getWorld());
				spell.addData(LOOK, direction);
				spell.addData(ORIGIN, new Vec3d(getPos()).addVector(0.5, 1.5, 0.5));
				spell.addData(CAPABILITY, cap);
				SpellUtils.runSpell(pearl, spell);
				setCooldown(world, null, null, pearl, spell);
			}
		}
	}
}
