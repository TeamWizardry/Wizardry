package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverrideInterface;

public interface IShapeOverrides {

	@ModuleOverrideInterface("shape_touch_render")
	boolean onRenderTouch(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_projectile_render")
	boolean onRenderProjectile(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_cone_render")
	boolean onRenderCone(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_beam_render")
	boolean onRenderBeam(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_zone_render")
	boolean onRenderZone(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_self_render")
	boolean onRenderSelf(SpellData data, SpellRing shape);
	
	@ModuleOverrideInterface("shape_self_run")
	void onRunSelf(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_touch_run")
	void onRunTouch(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_projectile_run")
	boolean onRunProjectile(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_beam_run")
	void onRunBeam(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_cone_run")
	void onRunCone(SpellData data, SpellRing shape);

	@ModuleOverrideInterface("shape_zone_run")
	void onRunZone(SpellData data, SpellRing shape);
}
