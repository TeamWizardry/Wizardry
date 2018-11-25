package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@RegisterModule(ID="effect_sonic")
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

		if (targetEntity instanceof EntityLivingBase)
		{
			Minecraft.getMinecraft().player.sendChatMessage(potency + " - " + area);
			damageEntity((EntityLivingBase) targetEntity, caster, (float) potency);
		
			if (((EntityLivingBase) targetEntity).getHealth() <= 0)
			{
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
	
	private void damageEntity(EntityLivingBase target, Entity caster, float damage)
	{
		int invTime = target.hurtResistantTime;
		target.hurtResistantTime = 0;
		if (caster instanceof EntityLivingBase)
		{
			((EntityLivingBase) caster).setLastAttackedEntity(target);
			if (caster instanceof EntityPlayer)
				target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster).setMagicDamage(), damage);
			else
				target.attackEntityFrom(new DamageSource("generic").setMagicDamage(), damage);
		}
		else
			target.attackEntityFrom(new DamageSource("generic").setMagicDamage(), damage);
		target.hurtResistantTime = invTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		// TODO: EffectShatter Particles
//		World world = spell.world;
//		Vec3d position = spell.getTarget();
//
//		if (position == null) return;
//
//		ParticleBuilder glitter = new ParticleBuilder(10);
//		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
//		glitter.setCollision(true);
//		glitter.setCanBounce(true);
//		glitter.enableMotionCalculation();
//		glitter.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.05, -0.035), 0));
//
//		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 80, 0, (i, builder) -> {
//			builder.setLifetime(RandUtil.nextInt(30, 60));
//			builder.addMotion(new Vec3d(RandUtil.nextDouble(-0.05, 0.05), RandUtil.nextDouble(0.01, 0.02), RandUtil.nextDouble(-0.05, 0.05)));
//			builder.setScale((float) RandUtil.nextDouble(0.3, 0.5));
//			builder.setAlphaFunction(new InterpFloatInOut(0.0f, 0.3f));
//			builder.setColor(RandUtil.nextBoolean() ? spellRing.getPrimaryColor() : spellRing.getSecondaryColor());
//		});
	}
}
