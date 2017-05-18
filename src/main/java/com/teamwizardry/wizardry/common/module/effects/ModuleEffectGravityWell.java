package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.AxisAlignedBB;
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
public class ModuleEffectGravityWell extends Module implements IlingeringModule {

	public ModuleEffectGravityWell() {
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.BLUE;
	}

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.SLIME_BALL);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_gravity_well";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Gravity Well";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will suck in all entities around the target.";
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
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double strength = 20;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(position)).expand(strength, strength, strength))) {
			if (entity == null) continue;
			double dist = entity.getPositionVector().distanceTo(position);
			if (dist > strength) continue;
			Vec3d direction = position.subtract(entity.getPositionVector());
			Vec3d dir = direction.subtract(entity.getPositionVector());
			entity.motionX = (dir.xCoord);
			entity.motionY = (dir.yCoord);
			entity.motionZ = (dir.zCoord);
			entity.fallDistance = 0;
			entity.velocityChanged = true;

			spell.addData(ENTITY_HIT, entity);
			if (entity instanceof EntityPlayerMP)
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));

			runNextModule(spell);
		}

		return true;
	}

	@Override
	public void runClient(@Nullable ItemStack stack, @Nonnull SpellData spell) {
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;
		LibParticles.FAIRY_TRAIL(spell.world, position, getColor(), false, 50);
	}

	@Nonnull
	@Override
	public ModuleEffectGravityWell copy() {
		ModuleEffectGravityWell module = new ModuleEffectGravityWell();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}

	@Override
	public int lingeringTime(SpellData spell) {
		int strength = 3000;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		return strength;
	}
}
