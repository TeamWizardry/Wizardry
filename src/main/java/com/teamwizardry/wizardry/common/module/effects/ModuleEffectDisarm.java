package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.ENTITY_HIT;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectDisarm extends Module {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_disarm";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Disarm";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will drop the target's held item on the ground";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity targetEntity = spell.getData(ENTITY_HIT);

		if (targetEntity instanceof EntityLivingBase) {
			if (!spell.world.isRemote) {

				ItemStack held = ((EntityLivingBase) targetEntity).getHeldItemMainhand();
				ItemStack stack = held.copy();
				stack.setCount(1);
				held.shrink(1);

				EntityItem item = new EntityItem(spell.world, targetEntity.posX, targetEntity.posY + 1, targetEntity.posZ, stack);
				item.setDefaultPickupDelay();
				return spell.world.spawnEntity(item);
			}
		}

		return false;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, getPrimaryColor());
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectDisarm());
	}
}
