package com.teamwizardry.wizardry.common.tile;

import com.mojang.authlib.GameProfile;
import com.teamwizardry.librarianlib.common.base.block.TileMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister;
import com.teamwizardry.librarianlib.common.util.saving.Save;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.block.IManaSink;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import com.teamwizardry.wizardry.init.ModBlocks;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by Saad on 5/7/2016.
 */
@TileRegister("pedestal")
public class TilePedestal extends TileMod implements ITickable {

	@Nullable
	@Save
	public ItemStack pearl;
	private FakePlayer fakePlayer;
	private int castCooldown;

	@Override
	public void update() {
        if (world.isRemote) return;
        if (pearl == null) return;

		if (pearl.getItem() instanceof Infusable) {

			NBTTagCompound spellCompound = ItemNBTHelper.getCompound(pearl, NBT.SPELL, true);
			if (spellCompound == null) return;
			SpellStack spell = new SpellStack(fakePlayer, fakePlayer, spellCompound);
			Module module = ModuleRegistry.getInstance().getModuleByLocation(spellCompound.getString(Constants.Module.SHAPE));

			if (!(module instanceof IContinuousCast)) {
				if (castCooldown > 0) {
					castCooldown--;
					return;
				}
				castCooldown = 20;
			}

			for (int i = -3; i < 3; i++)
				for (int j = -3; j < 3; j++)
					for (int k = -3; k < 3; k++) {
						BlockPos pos = new BlockPos(getPos().getX() + i, getPos().getY() + j, getPos().getZ() + k);
                        if (world.getBlockState(pos).getBlock() != ModBlocks.MANA_MAGNET) continue;

						castCooldown = 20;

						if (fakePlayer == null)
                            fakePlayer = FakePlayerFactory.get((WorldServer) world, new GameProfile(UUID.randomUUID(), "a pearl in a pedestal"));

						fakePlayer.posX = getPos().getX() + 0.5;
						fakePlayer.posY = getPos().getY() + 0.5;
						fakePlayer.posZ = getPos().getZ() + 0.5;

						Vec3d direction = new Vec3d(getPos()).subtract(new Vec3d(pos)).normalize();
						float yaw = (float) (-Math.atan2(direction.xCoord, direction.zCoord) * 180 / Math.PI - 180) / 2;
						fakePlayer.rotationYaw = yaw;
						fakePlayer.setRotationYawHead(yaw);

						spell.castSpell();

						break;
					}

		} else if (pearl.getItem() != ModItems.PEARL_MANA) return;

		NBTTagCompound compound = pearl.getTagCompound();
		if (compound == null) return;
		if (!compound.hasKey("link_x") || !compound.hasKey("link_y") || !compound.hasKey("link_z")) return;

		BlockPos pos = new BlockPos(compound.getInteger("link_x"), compound.getInteger("link_y"), compound.getInteger("link_z"));
        IBlockState block = world.getBlockState(pos);
        if (!(block.getBlock() instanceof IManaSink)) return;

		// TODO
	}
}
