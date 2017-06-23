package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.wizardry.api.events.EntityMoveWithHeadingEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * Created by LordSaad.
 */
public class PotionSlippery extends PotionMod {

	public PotionSlippery() {
		super("slippery", false, 0xABFCF0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void entityMove(EntityMoveWithHeadingEvent event) {
		if (!event.entity.isPotionActive(this)) return;
		event.override = true;

		EntityLivingBase entity = event.entity;
		float strafe = event.strafe / 2.0f;
		float forward = event.forward / 2.0f;
		float slipperiness = 1.04f;
		if (entity.isServerWorld() || entity.canPassengerSteer()) {
			if (!entity.isInWater() || entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying) {
				if (!entity.isInLava() || entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying) {
					if (entity.isElytraFlying()) {
						if (entity.motionY > -0.5D) {
							entity.fallDistance = 1.0F;
						}

						Vec3d vec3d = entity.getLookVec();
						float f = entity.rotationPitch * 0.017453292F;
						double d6 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
						double d8 = Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
						double d1 = vec3d.lengthVector();
						float f4 = MathHelper.cos(f);
						f4 = (float) ((double) f4 * (double) f4 * Math.min(1.0D, d1 / 0.4D));
						entity.motionY += -0.08D + (double) f4 * 0.06D;

						if (entity.motionY < 0.0D && d6 > 0.0D) {
							double d2 = entity.motionY * -0.1D * (double) f4;
							entity.motionY += d2;
							entity.motionX += vec3d.x * d2 / d6;
							entity.motionZ += vec3d.z * d2 / d6;
						}

						if (f < 0.0F) {
							double d9 = d8 * (double) (-MathHelper.sin(f)) * 0.04D;
							entity.motionY += d9 * 3.2D;
							entity.motionX -= vec3d.x * d9 / d6;
							entity.motionZ -= vec3d.z * d9 / d6;
						}

						if (d6 > 0.0D) {
							entity.motionX += (vec3d.x / d6 * d8 - entity.motionX) * 0.1D;
							entity.motionZ += (vec3d.z / d6 * d8 - entity.motionZ) * 0.1D;
						}

						entity.motionX *= 0.9900000095367432D;
						entity.motionY *= 0.9800000190734863D;
						entity.motionZ *= 0.9900000095367432D;
						entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);

						if (entity.isCollidedHorizontally && !entity.world.isRemote) {
							double d10 = Math.sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ);
							double d3 = d8 - d10;
							float f5 = (float) (d3 * 10.0D - 3.0D);

							if (f5 > 0.0F) {
								//entity.playSound(entity.getFallSound((int) f5), 1.0F, 1.0F);
								entity.attackEntityFrom(DamageSource.FLY_INTO_WALL, f5);
							}
						}

						if (entity.onGround && !entity.world.isRemote) {
							//entity.setFlag(7, false);
						}
					} else {
						float f6 = 0.91F;
						BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(entity.posX, entity.getEntityBoundingBox().minY - 1.0D, entity.posZ);

						if (entity.onGround) {
							f6 = slipperiness * 0.91f; //entity.world.getBlockState(blockpos$pooledmutableblockpos).getBlock().slipperiness * 0.91F;
						}

						float f7 = 0.16277136F / (f6 * f6 * f6);
						float f8;

						if (entity.onGround) {
							f8 = entity.getAIMoveSpeed() * f7;
						} else {
							f8 = entity.jumpMovementFactor;
						}

						entity.moveRelative(strafe, forward, f8);
						f6 = 0.91F;

						if (entity.onGround) {
							f6 = slipperiness * 0.91f; //entity.world.getBlockState(blockpos$pooledmutableblockpos.setPos(entity.posX, entity.getEntityBoundingBox().minY - 1.0D, entity.posZ)).getBlock().slipperiness * 0.91F;
						}

						if (entity.isOnLadder()) {
							float f9 = 0.15F;
							entity.motionX = MathHelper.clamp(entity.motionX, -0.15000000596046448D, 0.15000000596046448D);
							entity.motionZ = MathHelper.clamp(entity.motionZ, -0.15000000596046448D, 0.15000000596046448D);
							entity.fallDistance = 0.0F;

							if (entity.motionY < -0.15D) {
								entity.motionY = -0.15D;
							}

							boolean flag = entity.isSneaking() && entity instanceof EntityPlayer;

							if (flag && entity.motionY < 0.0D) {
								entity.motionY = 0.0D;
							}
						}

						entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);

						if (entity.isCollidedHorizontally && entity.isOnLadder()) {
							entity.motionY = 0.2D;
						}

						if (entity.isPotionActive(MobEffects.LEVITATION)) {
							entity.motionY += (0.05D * (double) (entity.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - entity.motionY) * 0.2D;
						} else {
							blockpos$pooledmutableblockpos.setPos(entity.posX, 0.0D, entity.posZ);

							if (!entity.world.isRemote || entity.world.isBlockLoaded(blockpos$pooledmutableblockpos) && entity.world.getChunkFromBlockCoords(blockpos$pooledmutableblockpos).isLoaded()) {
								if (!entity.hasNoGravity()) {
									entity.motionY -= 0.08D;
								}
							} else if (entity.posY > 0.0D) {
								entity.motionY = -0.1D;
							} else {
								entity.motionY = 0.0D;
							}
						}

						entity.motionY *= 0.9800000190734863D;
						entity.motionX *= (double) f6;
						entity.motionZ *= (double) f6;
						blockpos$pooledmutableblockpos.release();
					}
				} else {
					double d4 = entity.posY;
					entity.moveRelative(strafe, forward, 0.02F);
					entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
					entity.motionX *= 0.5D;
					entity.motionY *= 0.5D;
					entity.motionZ *= 0.5D;

					if (!entity.hasNoGravity()) {
						entity.motionY -= 0.02D;
					}

					if (entity.isCollidedHorizontally && entity.isOffsetPositionInLiquid(entity.motionX, entity.motionY + 0.6000000238418579D - entity.posY + d4, entity.motionZ)) {
						entity.motionY = 0.30000001192092896D;
					}
				}
			} else {
				double d0 = entity.posY;
				float f1 = 0.8f;//entity.getWaterSlowDown();
				float f2 = 0.02F;
				float f3 = (float) EnchantmentHelper.getDepthStriderModifier(entity);

				if (f3 > 3.0F) {
					f3 = 3.0F;
				}

				if (!entity.onGround) {
					f3 *= 0.5F;
				}

				if (f3 > 0.0F) {
					f1 += (0.54600006F - f1) * f3 / 3.0F;
					f2 += (entity.getAIMoveSpeed() - f2) * f3 / 3.0F;
				}

				entity.moveRelative(strafe, forward, f2);
				entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
				entity.motionX *= (double) f1;
				entity.motionY *= 0.800000011920929D;
				entity.motionZ *= (double) f1;

				if (!entity.hasNoGravity()) {
					entity.motionY -= 0.02D;
				}

				if (entity.isCollidedHorizontally && entity.isOffsetPositionInLiquid(entity.motionX, entity.motionY + 0.6000000238418579D - entity.posY + d0, entity.motionZ)) {
					entity.motionY = 0.30000001192092896D;
				}
			}
		}

		entity.prevLimbSwingAmount = entity.limbSwingAmount;
		double d5 = entity.posX - entity.prevPosX;
		double d7 = entity.posZ - entity.prevPosZ;
		float f10 = MathHelper.sqrt(d5 * d5 + d7 * d7) * 4.0F;

		if (f10 > 1.0F) {
			f10 = 1.0F;
		}

		entity.limbSwingAmount += (f10 - entity.limbSwingAmount) * 0.4F;
		entity.limbSwing += entity.limbSwingAmount;
	}
}
