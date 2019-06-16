package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.spell.IOverrideCooldown;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_leap")
public class ModuleEffectLeap implements IModuleEffect, IOverrideCooldown {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency"};
	}

	@Override
	public int getNewCooldown(World world, @Nonnull SpellData spell, SpellRing ring) {
		Entity target = spell.getCaster(world);
		if (!(target instanceof EntityLivingBase))
			return 50;

		ItemStack stack = ((EntityLivingBase) target).getHeldItemMainhand();

		if (stack.isEmpty()
				|| !NBTHelper.hasNBTEntry(stack, "jump_count")
				|| !NBTHelper.hasNBTEntry(stack, "max_jumps")
				|| !NBTHelper.hasNBTEntry(stack, "jump_timer"))
			return 50;

		int jumpCount = NBTHelper.getInt(stack, "jump_count", 0);
		int maxJumps = NBTHelper.getInt(stack, "max_jumps", 0);

		if (jumpCount <= 1) {

			NBTHelper.removeNBTEntry(stack, "jump_timer");
			NBTHelper.removeNBTEntry(stack, "jump_count");
			NBTHelper.removeNBTEntry(stack, "max_jumps");
			return 50;
		}

		NBTHelper.setInt(stack, "jump_count", jumpCount - 1);
		return (maxJumps + 5) - jumpCount;
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d lookVec = spell.getData(LOOK);
		Entity target = spell.getVictim(world);

		if (!(target instanceof EntityLivingBase)) return true;

		ItemStack stack = ((EntityLivingBase) target).getHeldItemMainhand();
		if (stack.isEmpty()) return true;
		if (lookVec == null) return true;

		if (!target.hasNoGravity()) {
			double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
			if (!spellRing.taxCaster(world, spell, true)) return false;

			if (!NBTHelper.hasNBTEntry(stack, "jump_count")
					|| !NBTHelper.hasNBTEntry(stack, "max_jumps")
					|| !NBTHelper.hasNBTEntry(stack, "jump_timer")) {
				NBTHelper.setInt(stack, "jump_count", (int) potency);
				NBTHelper.setInt(stack, "max_jumps", (int) potency);
				NBTHelper.setInt(stack, "jump_timer", 200);
			}

			target.motionX += lookVec.x;
			target.motionY += 0.65;
			target.motionZ += lookVec.z;

			target.velocityChanged = true;
			target.fallDistance /= (1 + MathHelper.ceil(spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell) / 8));

			if (target instanceof EntityPlayerMP)
				((EntityPlayerMP) target).connection.sendPacket(new SPacketEntityVelocity(target));
			world.playSound(null, target.getPosition(), ModSounds.FLY, SoundCategory.NEUTRAL, 1, 1);
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);
		Entity entityHit = spell.getVictim(world);

		if (position == null) return;
		if (entityHit == null) return;

		if (!entityHit.hasNoGravity()) {
			Vec3d normal = new Vec3d(entityHit.motionX, entityHit.motionY, entityHit.motionZ).normalize().scale(1 / 2.0);

			LibParticles.AIR_THROTTLE(world, position, normal, instance.getPrimaryColor(), instance.getSecondaryColor(), 0.5);

		}
	}

	@SubscribeEvent
	public void tickEntity(LivingEvent.LivingUpdateEvent event) {
		ItemStack stack = event.getEntityLiving().getHeldItemMainhand();
		if (stack.isEmpty()) return;

		if (NBTHelper.hasNBTEntry(stack, "jump_timer")) {
			int x = NBTHelper.getInt(stack, "jump_timer", 0);

			if (x <= 0 || event.getEntityLiving().collidedVertically) {
				NBTHelper.removeNBTEntry(stack, "jump_timer");
				NBTHelper.removeNBTEntry(stack, "jump_count");
				NBTHelper.removeNBTEntry(stack, "max_jumps");

			} else NBTHelper.setInt(stack, "jump_timer", x - 1);
		}
	}
}
