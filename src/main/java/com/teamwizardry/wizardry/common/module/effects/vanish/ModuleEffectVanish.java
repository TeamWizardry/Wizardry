package com.teamwizardry.wizardry.common.module.effects.vanish;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.network.PacketVanishPlayer;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_vanish")
public class ModuleEffectVanish implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@ModuleOverride("shape_zone_run")
	public boolean onRunZone(World world, SpellData data, SpellRing ring, @ContextRing SpellRing childRing) {
		double aoe = ring.getAttributeValue(world, AttributeRegistry.AREA, data);
		double range = ring.getAttributeValue(world, AttributeRegistry.RANGE, data);

		Vec3d targetPos = data.getTarget(world);

		if (targetPos == null) return false;

		BlockPos min = new BlockPos(targetPos.subtract(aoe, range, aoe));
		BlockPos max = new BlockPos(targetPos.add(aoe, range, aoe));

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(min, max));
		for (Entity entity : entities) {
			if (entity instanceof EntityLivingBase) {
				if (!VanishTracker.isVanished(entity) && entity.getDistanceSq(targetPos.x, targetPos.y, targetPos.z) <= aoe * aoe) {
					data.processEntity(entity, false);
					run(world, (ModuleInstanceEffect) childRing.getModule(), data, childRing);
				}
			}
		}
		return true;
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);

		double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 20;

		if (targetEntity instanceof EntityLivingBase) {
			if (!spellRing.taxCaster(world, spell, true)) return false;

			((EntityLivingBase) targetEntity).world.playSound(null, targetEntity.getPosition(), ModSounds.ETHEREAL_PASS_BY, SoundCategory.NEUTRAL, 0.5f, 1);
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) duration, 100, false, false));
			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, (int) duration, 100, false, false));
			VanishTracker.addVanishObject(targetEntity.getEntityId(), (int) duration);
			PacketHandler.NETWORK.sendToAll(new PacketVanishPlayer(targetEntity.getEntityId(), (int) duration));
			//	((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.VANISH, (int) duration, 0, true, false));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, instance.getPrimaryColor());
	}

}
