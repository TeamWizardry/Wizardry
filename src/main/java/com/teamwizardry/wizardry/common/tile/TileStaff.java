package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.spell.Spell;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.common.entity.EntityStaffFakePlayer;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

import static com.teamwizardry.wizardry.api.spell.Spell.DefaultKeys.*;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TileStaff extends TileMod implements ITickable {

	@Nullable
	@Save
	public ItemStack pearl;
	private EntityStaffFakePlayer fakePlayer = null;

	@Override
	public void update() {
		if (world.isRemote) return;
		if (pearl == null) return;
		if (pearl.getItem() == ModItems.PEARL_NACRE)
			for (int i = -3; i < 3; i++)
				for (int j = -3; j < 3; j++)
					for (int k = -3; k < 3; k++) {
						BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY() + j, getPos().getZ() + k);
						if (world.getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;

						if (fakePlayer == null)
							fakePlayer = new EntityStaffFakePlayer((WorldServer) getWorld());
						fakePlayer.setPosition(getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5);

						Vec3d direction = new Vec3d(getPos().subtract(pos)).normalize();
						fakePlayer.rotationYaw = (float) Math.atan2(direction.xCoord, direction.zCoord);

						Spell spell = new Spell(getWorld());
						spell.addData(CASTER, fakePlayer);
						spell.addData(ORIGIN, new Vec3d(getPos()).addVector(0.5, 0.5, 0.5));
						spell.addData(YAW, fakePlayer.rotationYaw);
						SpellStack.runModules(pearl, spell);
						break;
					}

	}
}
