package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstance;
import com.teamwizardry.wizardry.client.render.block.TileLightRenderer;

/**
 * Created by Demoniaque.
 */
@TileRenderer(TileLightRenderer.class)
@TileRegister(Wizardry.MODID + ":light")
public class TileLight extends TileMod {

	private ModuleInstance module = null;

	public void setModule(ModuleInstance module) {
		this.module = module;    // The light color is inherited from this given module
	}

	public ModuleInstance getModule() {
		return this.module;
	}
}
