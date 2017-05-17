package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.common.entity.EntitySpellProjectile;
import net.minecraft.client.Minecraft;
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

		Entity caster = spell.getData(CASTER);
		if (caster == null) return false;

		EntitySpellProjectile proj = new EntitySpellProjectile(world, this, spell);
		proj.setPosition(caster.posX + (caster.width / 2), caster.posY + caster.getEyeHeight(), caster.posZ + (caster.width / 2));
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
