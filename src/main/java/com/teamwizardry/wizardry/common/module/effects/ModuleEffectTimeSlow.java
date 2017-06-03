package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.network.PacketFreezePlayer;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectTimeSlow extends Module implements ITaxing {

	public ModuleEffectTimeSlow() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_time_slow";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Time Slow";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will slow down time depending on the strength. Can cause a complete freeze.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		if (targetEntity instanceof EntityLivingBase) {
			double strength = 50.0 * getMultiplier();
			if (attributes.hasKey(Attributes.EXTEND))
				strength += Math.min(200.0, attributes.getDouble(Attributes.EXTEND) * 4.6875);

			strength *= calcBurnoutPercent(caster);

			if (!tax(this, spell)) return false;
			int interval = (int) (50000 - (strength * 166.6666667));
			if (targetEntity instanceof EntityPlayer) interval /= 10.0;

			targetEntity.getEntityData().setInteger("strength", (int) strength);
			targetEntity.getEntityData().setInteger("skip_tick", (int) strength);
			targetEntity.getEntityData().setInteger("skip_tick_interval", interval);
			targetEntity.getEntityData().setInteger("skip_tick_interval_save", interval);

			if (targetEntity instanceof EntityPlayer)
				PacketHandler.NETWORK.sendTo(new PacketFreezePlayer((int) strength, interval), (EntityPlayerMP) targetEntity);
		}
		if (targetPos != null) {
			//BlockPos pos = new BlockPos(targetPos);
			//if (world.getBlockState(pos).getBlock() instanceof IGrowable)
			//	ItemDye.applyBonemeal(new ItemStack(Items.DYE), world, pos);
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(20, 50), 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(RandUtil.nextInt(10, 40));
			glitter.setScale(RandUtil.nextFloat());
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, RandUtil.nextFloat()));

			double radius = RandUtil.nextDouble(2, 3);
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			Vec3d dest = new Vec3d(x, radius, z);
			glitter.setPositionOffset(dest);
			glitter.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, position, dest.scale(2), new Vec3d(position.xCoord, radius, position.zCoord)));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectTimeSlow());
	}


	@SubscribeEvent
	public void skipTick(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity().getEntityData().hasKey("skip_tick")
				&& event.getEntity().getEntityData().hasKey("skip_tick_interval")
				&& event.getEntity().getEntityData().hasKey("skip_tick_interval_save")) {
			int tickCountdown = event.getEntity().getEntityData().getInteger("skip_tick");
			int tickInterval = event.getEntity().getEntityData().getInteger("skip_tick_interval");

			if (tickInterval <= 0) {
				event.getEntity().getEntityData().setInteger("skip_tick_interval", event.getEntity().getEntityData().getInteger("skip_tick_interval_save"));

				if (tickCountdown <= 0) {
					event.getEntity().getEntityData().removeTag("skip_tick");
					event.getEntity().getEntityData().removeTag("skip_tick_interval");
					event.getEntity().getEntityData().removeTag("skip_tick_interval_save");
				} else {
					event.getEntity().getEntityData().setInteger("skip_tick", --tickCountdown);
					event.setCanceled(true);
				}
			} else {
				event.getEntity().getEntityData().setInteger("skip_tick_interval", --tickInterval);
			}
		}
	}

	@SubscribeEvent
	public void skipPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.getEntityData().hasKey("skip_tick")
				&& event.player.getEntityData().hasKey("skip_tick_interval")
				&& event.player.getEntityData().hasKey("skip_tick_interval_save")) {
			int tickCountdown = event.player.getEntityData().getInteger("skip_tick");
			int tickInterval = event.player.getEntityData().getInteger("skip_tick_interval");

			event.player.addPotionEffect(new PotionEffect(ModPotions.NULL_MOVEMENT, 5, 1, true, false));
			event.player.addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 1, true, false));
			event.player.motionX = 0;
			event.player.motionY = 0;
			event.player.motionZ = 0;
			//event.player.rotationPitch = event.player.getEntityData().getFloat("rot_pitch");
			//event.player.rotationYaw = event.player.getEntityData().getFloat("rot_yaw");
			event.player.velocityChanged = true;

			if (tickInterval <= 0) {
				event.player.getEntityData().setInteger("skip_tick_interval", event.player.getEntityData().getInteger("skip_tick_interval_save"));

				if (tickCountdown <= 0) {
					event.player.getEntityData().removeTag("skip_tick");
					event.player.getEntityData().removeTag("skip_tick_interval");
					event.player.getEntityData().removeTag("skip_tick_interval_save");
					event.player.getEntityData().removeTag("strength");
				} else {
					Minecraft.getMinecraft().player.sendChatMessage(tickInterval + " - " + tickCountdown + " -  stop ticking");
					event.player.getEntityData().setInteger("skip_tick", --tickCountdown);
				}
			} else {
				event.player.getEntityData().setInteger("skip_tick_interval", --tickInterval);
			}
		}
	}

	@SubscribeEvent
	public void interact(PlayerInteractEvent event) {
		if (event.getEntity().getEntityData().hasKey("skip_tick")
				&& event.getEntity().getEntityData().hasKey("skip_tick_interval")
				&& event.getEntity().getEntityData().hasKey("skip_tick_interval_save")) {
			event.setCanceled(true);
		}
	}
}
