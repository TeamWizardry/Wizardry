package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.api.events.EntityMoveEvent;
import com.teamwizardry.wizardry.api.events.PlayerClipEvent;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionPhase extends PotionBase {

	public PotionPhase() {
		super("phase", false, 0xDAEFE7);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@SubscribeEvent
	public void playerClipEvent(PlayerClipEvent event) {
		if (event.player.isPotionActive(ModPotions.PHASE)) {
			event.noClip = true;
		}
	}

	@SubscribeEvent
	public void entityMove(EntityMoveEvent event) {
		if (!(event.entity instanceof EntityLivingBase)) return;
		EntityLivingBase base = (EntityLivingBase) event.entity;
		if (!base.isPotionActive(ModPotions.PHASE)) return;

		event.override = true;
		//event.entity.noClip = true;
		event.entity.fallDistance = 0;
		event.entity.isAirBorne = true;

		Entity entity = event.entity;
		double x = event.x;
		double y = event.y;
		double z = event.z;

		MoverType type = event.type;

		entity.world.profiler.startSection("move");
		double d10 = entity.posX;
		double d11 = entity.posY;
		double d1 = entity.posZ;

		double d2 = x;
		double d3 = y;
		double d4 = z;

		if ((type == MoverType.SELF || type == MoverType.PLAYER) && entity.onGround && entity.isSneaking() && entity instanceof EntityPlayer) {
			for (; x != 0.0D && entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, (double) (-entity.stepHeight), 0.0D)).isEmpty(); d2 = x) {
				if (x >= 0.05D || x < -0.05D) {
					if (x > 0.0D) {
						x -= 0.05D;
					} else {
						x += 0.05D;
					}
				}
			}

			for (; z != 0.0D && entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(0.0D, (double) (-entity.stepHeight), z)).isEmpty(); d4 = z) {
				if (z >= 0.05D || z < -0.05D) {
					if (z > 0.0D) {
						z -= 0.05D;
					} else {
						z += 0.05D;
					}
				}
			}

			for (; x != 0.0D && z != 0.0D && entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, (double) (-entity.stepHeight), z)).isEmpty(); d4 = z) {
				if (x >= 0.05D || x < -0.05D) {
					if (x > 0.0D) {
						x -= 0.05D;
					} else {
						x += 0.05D;
					}
				}

				d2 = x;

				if (z >= 0.05D || z < -0.05D) {
					if (z > 0.0D) {
						z -= 0.05D;
					} else {
						z += 0.05D;
					}
				}
			}
		}

		List<AxisAlignedBB> list1 = entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, y, z));
		AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();

		if (y != 0.0D) {
			int k = 0;

			for (int l = list1.size(); k < l; ++k) {
				double offsetY = list1.get(k).calculateYOffset(entity.getEntityBoundingBox(), y);
				if (offsetY <= 0)
					y = offsetY;
			}

			entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, y, 0.0D));
		}

		if (x != 0.0D) entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
		if (z != 0.0D) entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, 0.0D, z));

		boolean flag = entity.onGround || d3 != y && d3 < 0.0D;

		if (entity.stepHeight > 0.0F && flag && (d2 != x || d4 != z)) {
			double d14 = x;
			double d6 = y;
			double d7 = z;
			AxisAlignedBB axisalignedbb1 = entity.getEntityBoundingBox();
			entity.setEntityBoundingBox(axisalignedbb);
			y = (double) entity.stepHeight;
			List<AxisAlignedBB> list = entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(d2, y, d4));
			AxisAlignedBB axisalignedbb2 = entity.getEntityBoundingBox();
			AxisAlignedBB axisalignedbb3 = axisalignedbb2.offset(d2, 0.0D, d4);
			double d8 = y;
			int j1 = 0;

			for (int k1 = list.size(); j1 < k1; ++j1) {
				double offsetY = list1.get(j1).calculateYOffset(axisalignedbb3, y);
				if (offsetY <= 0)
					d8 = offsetY;
			}

			axisalignedbb2 = axisalignedbb2.offset(0.0D, d8, 0.0D);
			axisalignedbb2 = axisalignedbb2.offset(d2, 0.0D, 0.0D);
			axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, d4);
			AxisAlignedBB axisalignedbb4 = entity.getEntityBoundingBox();
			double d20 = y;
			int l2 = 0;

			for (int i3 = list.size(); l2 < i3; ++l2) {
				double offsetY = list1.get(i3).calculateYOffset(axisalignedbb4, y);
				if (offsetY <= 0)
					d20 = offsetY;
			}

			axisalignedbb4 = axisalignedbb4.offset(0.0D, d20, 0.0D);
			axisalignedbb4 = axisalignedbb4.offset(d2, 0.0D, 0.0D);
			axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d4);
			double d23 = d2 * d2 + d4 * d4;
			double d9 = d2 * d2 + d4 * d4;

			if (d23 > d9) {
				x = d2;
				z = d4;
				y = -d8;
				entity.setEntityBoundingBox(axisalignedbb2);
			} else {
				x = d2;
				z = d4;
				y = -d20;
				entity.setEntityBoundingBox(axisalignedbb4);
			}

			int j4 = 0;

			for (int k4 = list.size(); j4 < k4; ++j4) {
				double offsetY = list1.get(j4).calculateYOffset(entity.getEntityBoundingBox(), y);
				if (offsetY <= 0)
					y = offsetY;
			}

			entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, y, 0.0D));

			if (d14 * d14 + d7 * d7 >= x * x + z * z) {
				x = d14;
				y = d6;
				z = d7;
				entity.setEntityBoundingBox(axisalignedbb1);
			}
		}

		entity.world.profiler.endSection();
		entity.world.profiler.startSection("rest");
		entity.resetPositionToBB();
		entity.collidedHorizontally = d2 != x || d4 != z;
		entity.collidedVertically = d3 != y;
		entity.onGround = entity.collidedVertically && d3 < 0.0D;
		entity.collided = entity.collidedHorizontally || entity.collidedVertically;
		int j6 = MathHelper.floor(entity.posX);
		int i1 = MathHelper.floor(entity.posY - 0.20000000298023224D);
		int k6 = MathHelper.floor(entity.posZ);
		BlockPos blockpos = new BlockPos(j6, i1, k6);
		IBlockState iblockstate = entity.world.getBlockState(blockpos);

		if (iblockstate.getMaterial() == Material.AIR) {
			BlockPos blockpos1 = blockpos.down();
			IBlockState iblockstate1 = entity.world.getBlockState(blockpos1);
			Block block1 = iblockstate1.getBlock();

			if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate) {
				iblockstate = iblockstate1;
				blockpos = blockpos1;
			}
		}

		Block block = iblockstate.getBlock();

		if (d3 != y) {
			block.onLanded(entity.world, entity);
		}

		if ((!entity.onGround || !entity.isSneaking() || !(entity instanceof EntityPlayer)) && !entity.isRiding()) {
			double d15 = entity.posX - d10;
			double d16 = entity.posY - d11;
			double d17 = entity.posZ - d1;

			if (block != Blocks.LADDER) {
				d16 = 0.0D;
			}

			if (block != null && entity.onGround) {
				block.onEntityWalk(entity.world, blockpos, entity);
			}

			entity.distanceWalkedModified = (float) ((double) entity.distanceWalkedModified + (double) MathHelper.sqrt(d15 * d15 + d17 * d17) * 0.6D);
			entity.distanceWalkedOnStepModified = (float) ((double) entity.distanceWalkedOnStepModified + (double) MathHelper.sqrt(d15 * d15 + d16 * d16 + d17 * d17) * 0.6D);
		}

		try {
			AxisAlignedBB bb = entity.getEntityBoundingBox();
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(bb.minX + 0.001D, bb.minY + 0.001D, bb.minZ + 0.001D);
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos1 = BlockPos.PooledMutableBlockPos.retain(bb.maxX - 0.001D, bb.maxY - 0.001D, bb.maxZ - 0.001D);
			BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos2 = BlockPos.PooledMutableBlockPos.retain();

			if (entity.world.isAreaLoaded(blockpos$pooledmutableblockpos, blockpos$pooledmutableblockpos1)) {
				for (int i = blockpos$pooledmutableblockpos.getX(); i <= blockpos$pooledmutableblockpos1.getX(); ++i) {
					for (int j = blockpos$pooledmutableblockpos.getY(); j <= blockpos$pooledmutableblockpos1.getY(); ++j) {
						for (int k = blockpos$pooledmutableblockpos.getZ(); k <= blockpos$pooledmutableblockpos1.getZ(); ++k) {
							blockpos$pooledmutableblockpos2.setPos(i, j, k);
							IBlockState state = entity.world.getBlockState(blockpos$pooledmutableblockpos2);

							try {
								state.getBlock().onEntityCollidedWithBlock(entity.world, blockpos$pooledmutableblockpos2, state, entity);
							} catch (Throwable throwable) {
								CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
								CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being collided with");
								CrashReportCategory.addBlockInfo(crashreportcategory, blockpos$pooledmutableblockpos2, state);
								throw new ReportedException(crashreport);
							}
						}
					}
				}
			}

			blockpos$pooledmutableblockpos.release();
			blockpos$pooledmutableblockpos1.release();
			blockpos$pooledmutableblockpos2.release();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			entity.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}

		entity.world.profiler.endSection();
		//event.entity.noClip = false;
	}

	@Nullable
	public BlockPos getBottomBlock(@Nonnull Entity entity) {
		boolean isSpectator = (entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator());
		if (isSpectator) return null;
		AxisAlignedBB bb = entity.getEntityBoundingBox();
		int mX = MathHelper.floor(bb.minX);
		int mY = MathHelper.floor(bb.minY);
		int mZ = MathHelper.floor(bb.minZ);
		for (int y2 = mY; y2 < bb.maxY; y2++) {
			for (int x2 = mX; x2 < bb.maxX; x2++) {
				for (int z2 = mZ; z2 < bb.maxZ; z2++) {
					BlockPos tmp = new BlockPos(x2, y2, z2);
					if (!entity.world.isAirBlock(tmp)) return tmp;
				}
			}
		}

		return null;
	}
}
