package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_gravity_well")
public class ModuleEffectGravityWell implements IModuleEffect, ILingeringModule {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe", "modifier_increase_potency", "modifier_extend_time"};
	}

	@Override
	public boolean runOnStart(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return spellRing.taxCaster(world, spell, true);
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return false;

		double area = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);

		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(position)).grow(area, area, area))) {
			if (entity == null) continue;
			double dist = entity.getPositionVector().distanceTo(position);
			if (dist < 2) continue;
			if (dist > area) continue;

			final double upperMag = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell) / 50.0;
			final double scale = 1.5;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);

			Vec3d dir = position.subtract(entity.getPositionVector()).normalize().scale(mag);

			entity.motionX += (dir.x);
			entity.motionY += (dir.y);
			entity.motionZ += (dir.z);
			entity.fallDistance = 0;
			entity.velocityChanged = true;

			spell.addData(ENTITY_HIT, entity.getEntityId());
			if (entity instanceof EntityPlayerMP)
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
		double maxPotency = spellRing.getModule() != null ? spellRing.getModule().getAttributeRanges().get(AttributeRegistry.POTENCY).max : 1;

		ParticleBuilder glitter = new ParticleBuilder(0);
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 10, 10, (aFloat, particleBuilder) -> {
			particleBuilder.setScale((float) RandUtil.nextDouble(0.3, 1));
			particleBuilder.setAlphaFunction(new InterpFloatInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
			particleBuilder.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			particleBuilder.setLifetime(RandUtil.nextInt(20, 30));
			particleBuilder.setScaleFunction(new InterpScale(1, 0));

			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			particleBuilder.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-radius, radius), z).normalize().scale(RandUtil.nextFloat((float) ((maxPotency - potency) / maxPotency / 10.0))));
			particleBuilder.addMotion(particleBuilder.getPositionOffset().scale(-1.0 / RandUtil.nextDouble(10, 30)));
		});
	}

	@Override
	public int getLingeringTime(World world, SpellData spell, SpellRing spellRing) {
		return (int) (spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10);
	}
}
