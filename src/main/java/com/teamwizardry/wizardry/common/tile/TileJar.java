package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.client.render.block.TileJarRenderer;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
@TileRenderer(TileJarRenderer.class)
@TileRegister(Wizardry.MODID + ":jar")
public class TileJar extends TileMod {

	@Save
	public boolean hasFairy = false;
	@Save
	public Color color = Color.WHITE;
	@Save
	public int age = 0;
}
