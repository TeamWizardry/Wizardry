package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.wizardry.api.events.EntityMoveEvent;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.module.effects.ModuleEffectTimeSlow;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
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
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demoniaque.
 */
public class PotionTimeSlow extends PotionBase {

	public PotionTimeSlow() {
		super("time_slow", false, 0xE8CA0D);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public List<ItemStack> getCurativeItems() {
		return new ArrayList<>();
	}

	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.SLOW_MOTION_IN, SoundCategory.NEUTRAL, 1f, 1);
	}

	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, @Nonnull AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		entityLivingBaseIn.world.playSound(null, entityLivingBaseIn.getPosition(), ModSounds.SLOW_MOTION_OUT, SoundCategory.NEUTRAL, 1f, 1);
	}

	@SubscribeEvent
	public void fall(LivingFallEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (!entity.isPotionActive(this)) return;

		PotionEffect effect = entity.getActivePotionEffect(this);
		if (effect == null) return;

		event.setDistance((float) (event.getDistance() / (effect.getAmplifier() + 0.5)));
	}

	@SubscribeEvent
	public void entityMove(EntityMoveEvent event) {
		if (!(event.entity instanceof EntityLivingBase)) return;
		EntityLivingBase base = (EntityLivingBase) event.entity;
		if (!base.isPotionActive(ModPotions.TIME_SLOW)) return;
		PotionEffect effect = base.getActivePotionEffect(ModPotions.TIME_SLOW);
		if (effect == null) return;

		ModuleEffectTimeSlow slow = (ModuleEffectTimeSlow) ModuleRegistry.INSTANCE.getModule("effect_time_slow");
		SpellData data = new SpellData(event.entity.world);
		data.processEntity(event.entity, false);

		SpellRing ring = new SpellRing(slow);
		ring.getModule().sendRenderPacket(data, ring);

		event.override = true;

		Entity entity = event.entity;
		double amp = effect.getAmplifier();
		double x = event.x / amp;
		double y = event.y / amp;
		double z = event.z / amp;

		MoverType type = event.type;

		if (entity.noClip) {
			entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, y, z));
			entity.resetPositionToBB();
		} else {
			entity.world.profiler.startSection("move");
			double d10 = entity.posX;
			double d11 = entity.posY;
			double d1 = entity.posZ;

			double d2 = x;
			double d3 = y;
			double d4 = z;

			if ((type == MoverType.SELF || type == MoverType.PLAYER) && entity.onGround && entity.isSneaking() && entity instanceof EntityPlayer) {
				for (; x != 0.0D && entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, (double) (-entity.stepHeight), 0.0D)).isEmpty(); d2 = x) {
					if (x < 0.05D && x >= -0.05D) {
						x = 0.0D;
					} else if (x > 0.0D) {
						x -= 0.05D;
					} else {
						x += 0.05D;
					}
				}

				for (; z != 0.0D && entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(0.0D, (double) (-entity.stepHeight), z)).isEmpty(); d4 = z) {
					if (z < 0.05D && z >= -0.05D) {
						z = 0.0D;
					} else if (z > 0.0D) {
						z -= 0.05D;
					} else {
						z += 0.05D;
					}
				}

				for (; x != 0.0D && z != 0.0D && entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, (double) (-entity.stepHeight), z)).isEmpty(); d4 = z) {
					if (x < 0.05D && x >= -0.05D) {
						x = 0.0D;
					} else if (x > 0.0D) {
						x -= 0.05D;
					} else {
						x += 0.05D;
					}

					d2 = x;

					if (z < 0.05D && z >= -0.05D) {
						z = 0.0D;
					} else if (z > 0.0D) {
						z -= 0.05D;
					} else {
						z += 0.05D;
					}
				}
			}

			List<AxisAlignedBB> list1 = entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().offset(x, y, z));
			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();

			if (y != 0.0D) {
				int k = 0;

				for (int l = list1.size(); k < l; ++k) {
					y = list1.get(k).calculateYOffset(entity.getEntityBoundingBox(), y);
				}

				entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, y, 0.0D));
			}

			if (x != 0.0D) {
				int j5 = 0;

				for (int l5 = list1.size(); j5 < l5; ++j5) {
					x = list1.get(j5).calculateXOffset(entity.getEntityBoundingBox(), x);
				}

				if (x != 0.0D) {
					entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
				}
			}

			if (z != 0.0D) {
				int k5 = 0;

				for (int i6 = list1.size(); k5 < i6; ++k5) {
					z = list1.get(k5).calculateZOffset(entity.getEntityBoundingBox(), z);
				}

				if (z != 0.0D) {
					entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, 0.0D, z));
				}
			}

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
					d8 = list.get(j1).calculateYOffset(axisalignedbb3, d8);
				}

				axisalignedbb2 = axisalignedbb2.offset(0.0D, d8, 0.0D);
				double d18 = d2;
				int l1 = 0;

				for (int i2 = list.size(); l1 < i2; ++l1) {
					d18 = list.get(l1).calculateXOffset(axisalignedbb2, d18);
				}

				axisalignedbb2 = axisalignedbb2.offset(d18, 0.0D, 0.0D);
				double d19 = d4;
				int j2 = 0;

				for (int k2 = list.size(); j2 < k2; ++j2) {
					d19 = list.get(j2).calculateZOffset(axisalignedbb2, d19);
				}

				axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, d19);
				AxisAlignedBB axisalignedbb4 = entity.getEntityBoundingBox();
				double d20 = y;
				int l2 = 0;

				for (int i3 = list.size(); l2 < i3; ++l2) {
					d20 = list.get(l2).calculateYOffset(axisalignedbb4, d20);
				}

				axisalignedbb4 = axisalignedbb4.offset(0.0D, d20, 0.0D);
				double d21 = d2;
				int j3 = 0;

				for (int k3 = list.size(); j3 < k3; ++j3) {
					d21 = list.get(j3).calculateXOffset(axisalignedbb4, d21);
				}

				axisalignedbb4 = axisalignedbb4.offset(d21, 0.0D, 0.0D);
				double d22 = d4;
				int l3 = 0;

				for (int i4 = list.size(); l3 < i4; ++l3) {
					d22 = list.get(l3).calculateZOffset(axisalignedbb4, d22);
				}

				axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d22);
				double d23 = d18 * d18 + d19 * d19;
				double d9 = d21 * d21 + d22 * d22;

				if (d23 > d9) {
					x = d18;
					z = d19;
					y = -d8;
					entity.setEntityBoundingBox(axisalignedbb2);
				} else {
					x = d21;
					z = d22;
					y = -d20;
					entity.setEntityBoundingBox(axisalignedbb4);
				}

				int j4 = 0;

				for (int k4 = list.size(); j4 < k4; ++j4) {
					y = list.get(j4).calculateYOffset(entity.getEntityBoundingBox(), y);
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

			if (d2 != x) {
				entity.motionX = 0.0D;
			}

			if (d4 != z) {
				entity.motionZ = 0.0D;
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
		}
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
