package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverrideInterface;
import net.minecraft.world.World;

public interface IShapeOverrides {
	
	@ModuleOverrideInterface("shape_touch_render")
	boolean onRenderTouch(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_projectile_render")
	boolean onRenderProjectile(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_cone_render")
	boolean onRenderCone(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_beam_render")
	boolean onRenderBeam(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_zone_render")
	boolean onRenderZone(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_self_render")
	boolean onRenderSelf(World world, SpellData data, SpellRing shape);
	
	@ModuleOverrideInterface("shape_self_run")
	void onRunSelf(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_touch_run")
	void onRunTouch(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_projectile_run")
	boolean onRunProjectile(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_beam_run")
	void onRunBeam(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_cone_run")
	void onRunCone(World world, SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_zone_run")
	void onRunZone(World world, SpellData data, SpellRing shape);
}
