package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverrideInterface;

public interface IShapeOverrides {

	@ModuleOverrideInterface("shape_touch_render")
	void onRenderTouch(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_projectile_render")
	void onRenderProjectile(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_cone_render")
	void onRenderCone(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_beam_render")
	void onRenderBeam(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_self_run")
	void onRunSelf(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_touch_run")
	void onRunTouch(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_projectile_run")
	void onRunProjectile(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_beam_run")
	void onRunBeam(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_cone_run")
	void onRunCone(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverrideInterface("shape_zone_run")
	void onRunZone(SpellData data, SpellRing shape, SpellRing childRing);

	
}
