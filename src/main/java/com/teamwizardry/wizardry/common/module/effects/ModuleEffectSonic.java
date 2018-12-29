package com.teamwizardry.wizardry.common.module.effects;

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
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

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

		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 2;
		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell) / 2;

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetEntity instanceof EntityLivingBase) {
			Minecraft.getMinecraft().player.sendChatMessage(potency + " - " + area);
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
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new InterpCircle(new Vec3d(0, 1, 0), look, 2, 1f, 0), 20, 0, (aFloat, particleBuilder) -> {
			particleBuilder.setLifetime(RandUtil.nextInt(15, 20));
			particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.5f, 1f), 0f));
			particleBuilder.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
			particleBuilder.setMotion(particleBuilder.getPositionOffset().scale(-1));
		});
	}
}
