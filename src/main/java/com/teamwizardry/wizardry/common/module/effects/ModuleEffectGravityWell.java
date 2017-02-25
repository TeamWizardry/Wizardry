package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.lib.LibParticles;
import net.minecraft.client.Minecraft;
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

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

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

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.SLIME_BALL);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_gravity_well";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Gravity Well";
	}

	@NotNull
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
	public boolean run(@NotNull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		if (position == null) return false;

		double strength = 20;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(position)).expand(strength, strength, strength))) {
			Minecraft.getMinecraft().player.sendChatMessage(entity + "");
			if (entity == null) continue;
			if (entity.getPositionVector().distanceTo(position) > strength) continue;

			Vec3d dir1 = position.subtract(entity.getPositionVector());
			Vec3d dir = dir1.scale(1 / strength);
			entity.motionX += (dir.xCoord) / 10.0;
			entity.motionY += (dir.yCoord) / 10.0;
			entity.motionZ += (dir.zCoord) / 10.0;
			entity.fallDistance = 0;
			entity.velocityChanged = true;

			if (entity instanceof EntityPlayerMP)
				((EntityPlayerMP) entity).connection.sendPacket(new SPacketEntityVelocity(entity));
		}

		return runNextModule(spell);
	}

	@Override
	public void runClient(@Nullable ItemStack stack, @NotNull SpellData spell) {
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;
		LibParticles.EFFECT_NULL_GRAV(spell.world, position, null, getColor());
	}

	@NotNull
	@Override
	public ModuleEffectGravityWell copy() {
		ModuleEffectGravityWell module = new ModuleEffectGravityWell();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}

	@Override
	public int lingeringTime(SpellData spell) {
		int strength = 50;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += attributes.getDouble(Attributes.EXTEND);

		return strength * 10;
	}
}
