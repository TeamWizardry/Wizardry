package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectRepel extends Module implements IContinousSpell {

	public ModuleEffectRepel() {
	}

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Blocks.PUMPKIN);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "Repel";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Repel";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will repel entities from the target.";
	}

	@Override
	public double getManaToConsume() {
		return 100;
	}

	@Override
	public double getBurnoutToFill() {
		return 500;
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.BLUE;
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);

		double strength = 0.3;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(20.0 / 10.0, attributes.getDouble(Attributes.EXTEND) / 10.0);
		strength *= calcBurnoutPercent(caster);

		if (targetEntity != null) {
			Vec3d look = caster != null ? caster.getLook(0) : PosUtils.vecFromRotations(pitch, yaw);
			look = look.normalize().scale(strength * 10);
			targetEntity.motionX = look.xCoord;
			targetEntity.motionY = look.yCoord;
			targetEntity.motionZ = look.zCoord;
		}
		return true;
	}

	@Override
	public void runClient(@Nullable ItemStack stack, @Nonnull SpellData spell) {
		if (ThreadLocalRandom.current().nextInt(15) != 0) return;

		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		//LibParticles.EFFECT_REGENERATE(world, position, getColor());
	}

	@Nonnull
	@Override
	public ModuleEffectRepel copy() {
		ModuleEffectRepel module = new ModuleEffectRepel();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
