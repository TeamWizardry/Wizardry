package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.BounceHandler;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.network.PacketAddBouncyBlock;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@RegisterModule(ID = "effect_bouncing")
public class ModuleEffectBouncing implements IModuleEffect, ILingeringModule {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean runOnce(ModuleInstance instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim();
		World world = spell.world;
		BlockPos pos = spell.getTargetPos();

		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (entity instanceof EntityLivingBase) {
			world.playSound(null, entity.getPosition(), ModSounds.SLIME_SQUISHING, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.6f, 1f), RandUtil.nextFloat(0.5f, 1f));
			((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.BOUNCING, (int) time, 0, true, false));
		}

		if (pos != null) {
			if (!BlockUtils.isAnyAir(world, pos)) {
				BounceHandler.addBounceHandler(world, pos, (int) time);
				PacketHandler.NETWORK.sendToAll(new PacketAddBouncyBlock(world, pos, (int) time));
			}
		}

		return true;
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity target = spell.getVictim();
		BlockPos pos = spell.getTargetPos();

		if (ClientTickHandler.getTicks() % 10 == 0) return;
		if (target instanceof EntityLivingBase && ((EntityLivingBase) target).isPotionActive(ModPotions.BOUNCING)) {

			ParticleBuilder glitter = new ParticleBuilder(30);
			glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

			InterpCircle circle = new InterpCircle(Vec3d.ZERO, new Vec3d(0, 1, 0), target.width, RandUtil.nextFloat(), RandUtil.nextFloat());
			for (Vec3d origin : circle.list(30)) {
				ParticleSpawner.spawn(glitter, world, new StaticInterp<>(target.getPositionVector()), 1, 0, (aFloat, particleBuilder) -> {
					particleBuilder.setLifetime(RandUtil.nextInt(20, 25));
					particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(1f, 1.5f), 0f));
					particleBuilder.setAlphaFunction(new InterpFloatInOut(aFloat, aFloat));

					particleBuilder.setMotion(origin.normalize().scale(1.0 / 5.0));
					particleBuilder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.01, 0.01), 0));
				});
			}
		}

		if (pos != null && !BlockUtils.isAnyAir(world, pos)) {
			ParticleBuilder glitter = new ParticleBuilder(30);
			glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

			AxisAlignedBB aabb = world.getBlockState(pos).getSelectedBoundingBox(world, pos);
			double ix = aabb.minX;
			double iy = aabb.minY;
			double iz = aabb.minZ;
			double ax = aabb.maxX;
			double ay = aabb.maxY;
			double az = aabb.maxZ;

			spawnParticles(new Vec3d(ix, iy, iz), new Vec3d(ix, ay, iz), glitter, world);
			spawnParticles(new Vec3d(ix, ay, iz), new Vec3d(ax, ay, iz), glitter, world);
			spawnParticles(new Vec3d(ax, ay, iz), new Vec3d(ax, iy, iz), glitter, world);
			spawnParticles(new Vec3d(ax, iy, iz), new Vec3d(ix, iy, iz), glitter, world);
			spawnParticles(new Vec3d(ix, iy, az), new Vec3d(ix, ay, az), glitter, world);
			spawnParticles(new Vec3d(ix, iy, az), new Vec3d(ax, iy, az), glitter, world);
			spawnParticles(new Vec3d(ax, iy, az), new Vec3d(ax, ay, az), glitter, world);
			spawnParticles(new Vec3d(ix, ay, az), new Vec3d(ax, ay, az), glitter, world);
			spawnParticles(new Vec3d(ix, iy, iz), new Vec3d(ix, iy, az), glitter, world);
			spawnParticles(new Vec3d(ix, ay, iz), new Vec3d(ix, ay, az), glitter, world);
			spawnParticles(new Vec3d(ax, iy, iz), new Vec3d(ax, iy, az), glitter, world);
			spawnParticles(new Vec3d(ax, ay, iz), new Vec3d(ax, ay, az), glitter, world);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(Vec3d pos1, Vec3d pos2, ParticleBuilder builder, World world) {
		ParticleSpawner.spawn(builder, world, new InterpLine(pos1, pos2), 10, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setLifetime(RandUtil.nextInt(20, 25));
			particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.3f, 0.5f), 0f));
			particleBuilder.setAlphaFunction(new InterpFloatInOut(aFloat, aFloat));
			//	particleBuilder.setMotion(new Vec3d(0.5, 0.5, 0.5).add(pos1.subtract(pos2)).normalize().scale(1.0 / 5.0));
		});
	}

	@Override
	public int getLingeringTime(SpellData spell, SpellRing spellRing) {
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;
		return (int) time;
	}
}
