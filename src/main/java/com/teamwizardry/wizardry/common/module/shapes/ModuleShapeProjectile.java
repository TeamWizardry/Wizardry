package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.Spell;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static com.teamwizardry.wizardry.api.spell.Spell.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
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
	public boolean run(@NotNull Spell spell) {
		if (nextModule == null) return true;
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Entity caster = spell.getData(CASTER);

		if (caster == null) return false;
		Vec3d position = caster.getPositionVector();
		position = position.addVector(0, caster.getEyeHeight(), 0);

		EntitySpellProjectile proj = new EntitySpellProjectile(world, this, spell);
		proj.setPosition(position.xCoord, position.yCoord, position.xCoord);
		proj.setHeadingFromThrower(caster, pitch, yaw, 0.0f, 1.5f, 1.0f);
		proj.velocityChanged = true;
		world.spawnEntity(proj);
		return true;
	}

	@Override
	public int getChargeUpTime() {
		return 50;
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
