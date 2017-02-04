package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectFling extends Module {

	public ModuleEffectFling() {
	}

	@Nullable
	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.CHORUS_FRUIT_POPPED);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@NotNull
	@Override
	public String getID() {
		return "effect_fling";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Fling";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will send the caster flying to the target location";
	}

	@Override
	public double getManaToConsume() {
		return 2000;
	}

	@Override
	public double getBurnoutToFill() {
		return 5000;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Vec3d target) {
		if (caster == null) return false;
		double v = 0.3;
		double g = 0.02;
		Vec3d origin = caster.getPositionVector();
		double distSquared = MathHelper.sqrt(
				(target.xCoord - origin.xCoord) * (target.xCoord - origin.xCoord))
				+ (target.zCoord - origin.zCoord) * (target.zCoord - origin.zCoord);
		double distSqrt = MathHelper.sqrt(distSquared);
		double det = (v * v * v * v) - (g * (g * distSquared + (2 * v * v * (target.yCoord - origin.yCoord))));
		double tan = ((v * v) + MathHelper.sqrt(det)) / (g * distSqrt);

		double y = tan / (MathHelper.sqrt((tan * tan) + 1));

		Vec3d axis = target.subtract(origin).normalize().scale(1 / ((tan * tan) + 1));
		double x = axis.xCoord;
		double z = axis.zCoord;

		Minecraft.getMinecraft().player.sendChatMessage(x + ", " + y + ", " + z);
		caster.motionX = x;
		caster.motionY = y;
		caster.motionZ = z;
		caster.velocityChanged = true;

		return true;
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {

		return true;
	}

	@Override
	public void runClient(@NotNull World world, @Nullable ItemStack stack, @Nullable EntityLivingBase caster, @NotNull Vec3d pos) {
	}

	@NotNull
	@Override
	public ModuleEffectFling copy() {
		ModuleEffectFling module = new ModuleEffectFling();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
