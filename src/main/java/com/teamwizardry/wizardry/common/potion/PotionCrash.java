package com.teamwizardry.wizardry.common.potion;

import com.teamwizardry.librarianlib.features.base.PotionMod;
import com.teamwizardry.wizardry.api.events.EntityPostMoveEvent;
import com.teamwizardry.wizardry.api.spell.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.SpellData;
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
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ReportedException;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static sun.audio.AudioPlayer.player;

/**
 * Created by LordSaad.
 */
public class PotionCrash extends PotionMod {

	public PotionCrash() {
		super("crash", false, 0x8C680f);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public DamageSource damageSourceEarthquake(EntityLivingBase player) {
		return new EntityDamageSource("wizardry.crash", player);
	}

	public DamageSource damageSourceEarthquake() {
		return new DamageSource("wizardry.crash");
	}

	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void fall(LivingFallEvent e) {
		EntityPlayer player;
		if (!(e.getEntityLiving() instanceof EntityPlayer)) return;
		player = (EntityPlayer) e.getEntityLiving();
		PotionEffect crash = player.getActivePotionEffect(this);
		if (crash != null) {
			PotionEffect jump = player.getActivePotionEffect(MobEffects.JUMP_BOOST);
			float f = (jump == null) ? 0.0f : (jump.getAmplifier() + 1);
			float damage = Math.min((e.getDistance() - 3.0f - f) * e.getDamageMultiplier(), 5.0f);
			if (damage > 0.0f) {
				e.setCanceled(true);
				List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().expand(4.0, 3.0, 4.0));
				entities.stream().filter(entity -> entity != player && entity.onGround).forEach(entity ->
						entity.attackEntityFrom(damageSourceEarthquake(player), damage * crash.getAmplifier() / 2f));

				player.attackEntityFrom(damageSourceEarthquake(), 0.00005f);

				for (BlockPos pos : BlockPos.getAllInBoxMutable(new BlockPos(player.getPositionVector()).add(-1, -1, -1), new BlockPos(player.getPositionVector()).add(1, -1, 1))) {
					IBlockState state = player.world.getBlockState(pos);
					if (state.isFullCube())
						player.world.playEvent(2001, pos, state.getBlock().getMetaFromState(state));
				}
			}
		}
	}
}
