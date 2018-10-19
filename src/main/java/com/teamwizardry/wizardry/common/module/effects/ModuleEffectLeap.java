package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.spell.IOverrideCooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.IModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreasePotency;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectLeap implements IModuleEffect, IOverrideCooldown {

	@Nonnull
	@Override
	public String getClassID() {
		return "effect_leap";
	}

	@Override
	public IModuleModifier[] applicableModifiers() {
		return new IModuleModifier[]{new ModuleModifierIncreasePotency()};
	}

	@Override
	public int getNewCooldown(@Nonnull SpellData spell, SpellRing ring) {
		Entity target = spell.getData(ENTITY_HIT);
		if (!(target instanceof EntityLivingBase))
			return 50;

		ItemStack stack = ((EntityLivingBase) target).getHeldItemMainhand();

		if (stack.isEmpty()
				|| !ItemNBTHelper.verifyExistence(stack, "jump_count")
				|| !ItemNBTHelper.verifyExistence(stack, "max_jumps")
				|| !ItemNBTHelper.verifyExistence(stack, "jump_timer"))
			return 50;

		int jumpCount = ItemNBTHelper.getInt(stack, "jump_count", 0);
		int maxJumps = ItemNBTHelper.getInt(stack, "max_jumps", 0);

		if (jumpCount <= 1) {

			ItemNBTHelper.removeEntry(stack, "jump_timer");
			ItemNBTHelper.removeEntry(stack, "jump_count");
			ItemNBTHelper.removeEntry(stack, "max_jumps");
			return 50;
		}

		ItemNBTHelper.setInt(stack, "jump_count", jumpCount - 1);
		return (maxJumps + 5) - jumpCount;
	}

	@Override
	public boolean run(ModuleEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d lookVec = spell.getData(LOOK);
		Entity target = spell.getVictim();

		if (target == null) return true;
		if (!(target instanceof EntityLivingBase)) return true;

		ItemStack stack = ((EntityLivingBase) target).getHeldItemMainhand();
		if (stack.isEmpty()) return true;
		if (lookVec == null) return true;

		if (!target.hasNoGravity()) {
			double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);
			if (!spellRing.taxCaster(spell, true)) return false;

			if (!ItemNBTHelper.verifyExistence(stack, "jump_count")
					|| !ItemNBTHelper.verifyExistence(stack, "max_jumps")
					|| !ItemNBTHelper.verifyExistence(stack, "jump_timer")) {
				ItemNBTHelper.setInt(stack, "jump_count", (int) potency);
				ItemNBTHelper.setInt(stack, "max_jumps", (int) potency);
				ItemNBTHelper.setInt(stack, "jump_timer", 200);
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
	public void renderSpell(ModuleEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget();
		Entity entityHit = spell.getVictim();

		if (position == null) return;
		if (entityHit == null) return;

		if (!entityHit.hasNoGravity()) {
			Vec3d normal = new Vec3d(entityHit.motionX, entityHit.motionY, entityHit.motionZ).normalize().scale(1 / 2.0);

			LibParticles.AIR_THROTTLE(spell.world, position, normal, instance.getPrimaryColor(), instance.getSecondaryColor(), 0.5);

		}
	}

	@SubscribeEvent
	public void tickEntity(LivingEvent.LivingUpdateEvent event) {
		ItemStack stack = event.getEntityLiving().getHeldItemMainhand();
		if (stack.isEmpty()) return;

		if (ItemNBTHelper.verifyExistence(stack, "jump_timer")) {
			int x = ItemNBTHelper.getInt(stack, "jump_timer", 0);

			if (x <= 0 || event.getEntityLiving().collidedVertically) {
				ItemNBTHelper.removeEntry(stack, "jump_timer");
				ItemNBTHelper.removeEntry(stack, "jump_count");
				ItemNBTHelper.removeEntry(stack, "max_jumps");

			} else ItemNBTHelper.setInt(stack, "jump_timer", x - 1);
		}
	}
}
