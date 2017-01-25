package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class ModuleEffectNullGrav extends Module implements ITargettable {

	public ModuleEffectNullGrav() {
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.DRAGON_BREATH);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_nullify_gravity";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Nullify Gravity";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will completely disable the target entity's gravity.";
	}

	@Override
	public double getManaToConsume() {
		return 500;
	}

	@Override
	public double getBurnoutToFill() {
		return 200;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Vec3d target) {
		return false;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @Nullable Entity target) {
		if (target != null && target instanceof EntityLivingBase) {
			((EntityLivingBase) target).addPotionEffect(new PotionEffect(ModPotions.NULLIFY_GRAVITY, 100, 3, false, false));
			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public ModuleEffectNullGrav copy() {
		ModuleEffectNullGrav module = new ModuleEffectNullGrav();
		module.deserializeNBT(serializeNBT());
		return module;
	}
}
