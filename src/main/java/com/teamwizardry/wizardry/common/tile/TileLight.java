package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.TileMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.InterpScale;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

/**
 * Created by LordSaad.
 */
@TileRegister("light")
public class TileLight extends TileMod implements ITickable {

	@Override
	public void update() {
		if (world.isRemote && RandUtil.nextInt(4) == 0) {
			ParticleBuilder glitter = new ParticleBuilder(30);
			glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, 0.3f));
			glitter.setColorFunction(new InterpColorHSV(Color.CYAN, Color.BLUE));
			glitter.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(1, 3), 0));
			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), 1, 0, (i, build) -> {
				glitter.setMotion(new Vec3d(
						RandUtil.nextDouble(-0.01, 0.01),
						RandUtil.nextDouble(0, 0.03),
						RandUtil.nextDouble(-0.01, 0.01)));
			});
		}
	}
}
