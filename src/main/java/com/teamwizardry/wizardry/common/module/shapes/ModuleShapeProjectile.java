package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class ModuleShapeProjectile extends Module {

	public ModuleShapeProjectile() {
		process(this);
	}

	@NotNull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.NETHER_WART);
	}

	@NotNull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@NotNull
	@Override
	public String getID() {
		return "shape_projectile";
	}

	@NotNull
	@Override
	public String getReadableName() {
		return "Projectile";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Will launch the spell as a projectile in the direction the caster is looking.";
	}

	@Override
	public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
		if (caster == null) return false;
		float yaw = caster.rotationYaw;
		float pitch = caster.rotationPitch;
		EntitySpellProjectile proj = new EntitySpellProjectile(world, caster, caster.getLookVec(), this);
		Vec3d pos = caster.getPositionVector().addVector(0, caster.getEyeHeight(), 0);
		proj.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
		proj.setDirection(yaw, pitch);
		world.spawnEntity(proj);
		return true;
	}

	@NotNull
	@Override
	public ModuleShapeProjectile copy() {
		ModuleShapeProjectile module = new ModuleShapeProjectile();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
