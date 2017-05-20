package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectRepel extends Module implements IContinousSpell {

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
	public double getManaDrain() {
		return 100;
	}

	@Override
	public double getBurnoutFill() {
		return 500;
	}

	@Nullable
	@Override
	public Color getPrimaryColor() {
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
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Entity targetEntity = spell.getData(ENTITY_HIT);
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);

		if (targetEntity == null) return;

		Vec3d position = targetEntity.getPositionVector().addVector(targetEntity.width / 2, targetEntity.height / 2, targetEntity.width / 2);

		LibParticles.AIR_THROTTLE(world, position, PosUtils.vecFromRotations(pitch, yaw), getPrimaryColor(), ColorUtils.shiftColorHueRandomly(getPrimaryColor(), 50), 1, true);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectRepel());
	}
}
