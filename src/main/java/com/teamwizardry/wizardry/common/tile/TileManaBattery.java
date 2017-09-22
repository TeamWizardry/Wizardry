package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.block.TileManaFaucet;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketExplode;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

@TileRegister("mana_battery")
public class TileManaBattery extends TileManaFaucet implements ITickable {

	public static HashSet<TileManaFaucet> FAUCETS = new HashSet<>();

	public static final ArrayList<BlockPos> poses = new ArrayList<>();

	static {
		poses.add(new BlockPos(2, 1, 2));
		poses.add(new BlockPos(-2, 1, 2));
		poses.add(new BlockPos(2, 1, -2));
		poses.add(new BlockPos(-2, 1, -2));

		poses.add(new BlockPos(4, 2, 4));
		poses.add(new BlockPos(-4, 2, 4));
		poses.add(new BlockPos(4, 2, -4));
		poses.add(new BlockPos(-4, 2, -4));
	}


	public TileManaBattery() {
		super(100000, 100000);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		if (!FAUCETS.contains(this)) FAUCETS.add(this);

		CapManager manager = new CapManager(cap);
		if (manager.isManaFull()) return;

		for (BlockPos relative : poses) {
			BlockPos target = getPos().add(relative);
			if (!world.isBlockLoaded(target)) continue;

			TileEntity tile = world.getTileEntity(target);
			if (tile == null) continue;
			if (!(tile instanceof TilePearlHolder)) continue;
			TilePearlHolder holder = (TilePearlHolder) world.getTileEntity(target);

			if (holder == null || holder.pearl == null || holder.pearl.isEmpty() || holder.pearl.getItem() != ModItems.MANA_ORB)
				continue;

			CapManager orbManager = new CapManager(holder.pearl);
			if (orbManager.getMana() <= 0) {
				holder.pearl = ItemStack.EMPTY;
				holder.markDirty();

				world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), ModSounds.GLASS_BREAK, SoundCategory.AMBIENT, 0.5F, (RandUtil.nextFloat() * 0.4F) + 0.8F);
				PacketHandler.NETWORK.sendToAllAround(new PacketExplode(new Vec3d(target).addVector(0.5, 0.5, 0.5), Color.CYAN, Color.BLUE, 0.5, 0.5, 50, 50, 10, true),
						new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));
			} else {
				if (!addMana(10)) break;
				else orbManager.removeMana(10);
			}
		}
	}
}
