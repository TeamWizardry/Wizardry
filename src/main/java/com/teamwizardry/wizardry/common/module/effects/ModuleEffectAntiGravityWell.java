package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.common.entity.EntitySpellGravityWell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.Spell.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.Spell.DefaultKeys.ORIGIN;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectAntiGravityWell extends Module {

	public ModuleEffectAntiGravityWell() {
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.MAGMA_CREAM);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_anti_gravity_well";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Anti Gravity Well";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will disperse in all entities around the target.";
	}

	@Override
	public double getManaToConsume() {
		return 1000;
	}

	@Override
	public double getBurnoutToFill() {
		return 1000;
	}

	@Override
	public boolean run(@NotNull Spell spell) {
		World world = spell.world;
		Vec3d position = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double strength = 20;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);
		EntitySpellGravityWell well = new EntitySpellGravityWell(world, caster instanceof EntityPlayer ? (EntityLivingBase) caster : null, position, (int) (strength * 20), strength, true);
		well.setPosition(position.xCoord, position.yCoord, position.zCoord);
		world.spawnEntity(well);
		setTargetPosition(this, position);
		return world.spawnEntity(well);
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
	}

	@NotNull
	@Override
	public ModuleEffectAntiGravityWell copy() {
		ModuleEffectAntiGravityWell module = new ModuleEffectAntiGravityWell();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
