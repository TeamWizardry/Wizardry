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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectAntiGravityWell extends Module implements IlingeringModule {

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
	public boolean run(@NotNull SpellData spell) {
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

			Vec3d dir1 = position.subtract(entity.getPositionVector());
			dir1.scale(1 / dist);
			Vec3d dir = dir1.scale(1 / strength);
			entity.motionX += (dir.xCoord) / 10.0;
			entity.motionY += (dir.yCoord) / 10.0;
			entity.motionZ += (dir.zCoord) / 10.0;
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
	public void runClient(@Nullable ItemStack stack, @NotNull SpellData spell) {
		Vec3d position = spell.getData(ORIGIN);

		if (position == null) return;
		LibParticles.EFFECT_NULL_GRAV(spell.world, position, null, getColor());
	}

	@NotNull
	@Override
	public ModuleEffectAntiGravityWell copy() {
		ModuleEffectAntiGravityWell module = new ModuleEffectAntiGravityWell();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}

	@Override
	public int lingeringTime(SpellData spell) {
		int strength = 500;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		return strength;
	}
}
