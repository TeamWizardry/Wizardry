package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.IOverrideCooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectLeap extends ModuleEffect implements IOverrideCooldown {

	@Nonnull
	@Override
	public String getID() {
		return "effect_leap";
	}
	
	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreasePotency()};
	}

	@Override
	public int getNewCooldown(@Nonnull SpellData spell, SpellRing ring) {
		Entity target = spell.getData(ENTITY_HIT);
		if (target == null)
			return 50;
		int jumpCount = target.getEntityData().getInteger("jump_count");
		if (jumpCount <= 0)
		{
			target.getEntityData().removeTag("jump_count");
			return 50;
		}
		target.getEntityData().setInteger("jump_count", jumpCount - 1);
		return 0;
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Entity target = spell.getVictim();

		if (target == null) return false;
		if (!(target instanceof EntityLivingBase)) return true;

		Vec3d lookVec = PosUtils.vecFromRotations(pitch, yaw);

		if (!target.hasNoGravity()) {
			double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 10;
			if (!spellRing.taxCaster(spell)) return false;

			if (!target.getEntityData().hasKey("jump_count")) {
				target.getEntityData().setInteger("jump_count", (int) potency);
				target.getEntityData().setInteger("jump_timer", 200);
			}

			target.motionX += lookVec.x;
			target.motionY += 0.65;
			target.motionZ += lookVec.z;

			target.velocityChanged = true;
			target.fallDistance /= (1 + MathHelper.ceil(spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell) / 8));

			if (target instanceof EntityPlayerMP)
				((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
			spell.world.playSound(null, target.getPosition(), ModSounds.FLY, SoundCategory.NEUTRAL, 1, 1);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget();
		Entity entityHit = spell.getVictim();

		if (position == null) return;
		if (entityHit == null) return;

		if (!entityHit.hasNoGravity()) {
			Vec3d normal = new Vec3d(entityHit.motionX, entityHit.motionY, entityHit.motionZ).normalize().scale(1 / 2.0);

			LibParticles.AIR_THROTTLE(spell.world, position, normal, getPrimaryColor(), getSecondaryColor(), 0.5);

		}
	}

	@SubscribeEvent
	public void tickEntity(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().getEntityData().hasKey("jump_timer")) {
			int x = event.getEntityLiving().getEntityData().getInteger("jump_timer");

			if (event.getEntityLiving().collidedVertically) {
				event.getEntityLiving().getEntityData().removeTag("jump_timer");
				event.getEntityLiving().getEntityData().removeTag("jump_count");
				return;
			}

			if (x <= 0) {
				event.getEntityLiving().getEntityData().removeTag("jump_timer");
				event.getEntityLiving().getEntityData().removeTag("jump_count");
			} else event.getEntityLiving().getEntityData().setInteger("jump_timer", x - 1);
		}
	}
}
