package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.ITaxing;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.attribute.Attributes;
import com.teamwizardry.wizardry.api.spell.module.*;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierExtendRange;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseSpeed;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleShapeProjectile extends ModuleShape implements ITaxing {

	@Nonnull
	@Override
	public String getID() {
		return "shape_projectile";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierExtendRange(), new ModuleModifierIncreaseSpeed()};
	}

	@Override
	@SuppressWarnings("unused")
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		if (world.isRemote) return true;

		Vec3d target = spell.getData(TARGET_HIT);
		Vec3d origin = spell.getData(ORIGIN);
		Entity caster = spell.getData(CASTER);
		if (origin == null) return true;

		double dist = getModifier(spell, Attributes.RANGE, 20, 240);
		double speed = getModifier(spell, Attributes.SPEED, 1, 10);

		EntitySpellProjectile proj = new EntitySpellProjectile(world, this, spell, dist, speed, 0.1);
		proj.setPosition(origin.x, origin.y, origin.z);
		proj.velocityChanged = true;

		if (!tax(this, spell)) return false;
		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, new BlockPos(origin), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) RandUtil.nextDouble(1, 1.5));
		return success;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, SpellRing spellRing) {

	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleShapeProjectile());
	}
}
