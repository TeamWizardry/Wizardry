package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;

public interface IShapeOverrides {

	@ModuleOverride("shape_touch_render")
	void onRenderTouch(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_projectile_render")
	void onRenderProjectile(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_cone_render")
	void onRenderCone(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_beam_render")
	void onRenderBeam(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_self_run")
	void onRunSelf(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_touch_run")
	void onRunTouch(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_projectile_run")
	void onRunProjectile(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_beam_run")
	void onRunBeam(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_cone_run")
	void onRunCone(SpellData data, SpellRing shape, SpellRing childRing);

	@ModuleOverride("shape_zone_run")
	void onRunZone(SpellData data, SpellRing shape, SpellRing childRing);

	
}
