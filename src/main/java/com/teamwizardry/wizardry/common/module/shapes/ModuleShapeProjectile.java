package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleShapeProjectile extends Module {

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.NETHER_WART);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.SHAPE;
	}

	@Nonnull
	@Override
	public String getID() {
		return "shape_projectile";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Projectile";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will launch the spell as a projectile in the direction the caster is looking.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		if (nextModule == null) return true;
		World world = spell.world;
		if (world.isRemote) return false;

		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Vec3d origin = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);

		if (origin == null) return false;

		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(origin);
		}

		EntitySpellProjectile proj = new EntitySpellProjectile(world, this, spell);
		proj.setPosition(origin.xCoord, origin.yCoord, origin.xCoord);
		float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		float f1 = -MathHelper.sin((pitch) * 0.017453292F);
		float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
		proj.setThrowableHeading((double) f, (double) f1, (double) f2, 1.5f, 1.0f);
		if (caster != null) {
			proj.motionX += caster.motionX;
			proj.motionZ += caster.motionZ;

			if (!caster.onGround) proj.motionY += caster.motionY;
		}
		proj.velocityChanged = true;

		usedShape = this;
		return world.spawnEntity(proj);
	}

	@Override
	public int getChargeUpTime() {
		return 50;
	}

	@Nonnull
	@Override
	public ModuleShapeProjectile copy() {
		ModuleShapeProjectile module = new ModuleShapeProjectile();
		module.deserializeNBT(serializeNBT());
		process(module);
		return module;
	}
}
