package com.teamwizardry.wizardry.common.module.effects;

import javax.annotation.Nonnull;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.init.ModSounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegisterModule(ID = "effect_sonic")
public class ModuleEffectSonic implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", "modifier_increase_aoe"};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim();
		Entity caster = spell.getCaster();
		World world = spell.world;
		BlockPos pos = spell.getTargetPos();

		if (pos == null) return false;

		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 2;
		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell) / 2;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			world.playSound(null, pos, ModSounds.SOUND_BOMB, SoundCategory.NEUTRAL, 1, RandUtil.nextFloat(0.8f, 1.2f));
			damageEntity((EntityLivingBase) targetEntity, caster, (float) potency);

			if (((EntityLivingBase) targetEntity).getHealth() <= 0) {
				Vec3d targetPos = targetEntity.getPositionVector();
				double sqArea = area * area;
				AxisAlignedBB aabb = new AxisAlignedBB(targetEntity.getPosition()).grow(area);
				world.getEntitiesWithinAABB(EntityLivingBase.class, aabb).stream()
						.filter(entity -> entity.getPositionVector().squareDistanceTo(targetPos) < sqArea)
						.forEach(entity -> damageEntity(entity, caster, (float) potency));
			}
		}
		return true;
	}

	private void damageEntity(EntityLivingBase target, Entity caster, float damage) {
		int invTime = target.hurtResistantTime;
		target.hurtResistantTime = 0;
		if (caster instanceof EntityLivingBase) {
			((EntityLivingBase) caster).setLastAttackedEntity(target);
			if (caster instanceof EntityPlayer)
				target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster).setMagicDamage(), damage);
			else
				target.attackEntityFrom(new DamageSource("generic").setMagicDamage(), damage);
		} else
			target.attackEntityFrom(new DamageSource("generic").setMagicDamage(), damage);
		target.hurtResistantTime = invTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity target = spell.getVictim();
		Vec3d look = spell.getData(SpellData.DefaultKeys.LOOK);

		if (target == null || look == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.DIAMOND));
		glitter.setDeceleration(new Vec3d(0.5, 0.5, 0.5));
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.01, 0.01), 0));

		Vec3d entityOrigin = target.getPositionVector().add(target.width / 2.0, target.height / 2.0, target.width / 2.0);
		InterpCircle circle = new InterpCircle(Vec3d.ZERO, new Vec3d(0, 1, 0), target.width);

		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell) / 2;
		for (Vec3d origin : circle.list(50)) {
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(entityOrigin.add(origin)), 5, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setLifetime(RandUtil.nextInt(50, 100));
				particleBuilder.setScale(RandUtil.nextFloat(0.25f, 1));
				particleBuilder.setAlphaFunction(new InterpFloatInOut(0.1f, 0.5f));
				particleBuilder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.01, 0.01), 0));

				particleBuilder.setMotion(origin.normalize().scale(RandUtil.nextDouble(0, area / 2.0)));

				particleBuilder.setJitter(1, new Vec3d(RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(-0.01, 0.01), RandUtil.nextDouble(-0.01, 0.01)));
			});
		}
	}
}
