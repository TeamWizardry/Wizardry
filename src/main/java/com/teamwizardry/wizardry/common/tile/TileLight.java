package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
@TileRegister("light")
public class TileLight extends TileMod implements ITickable {

	@Override
	public void update() {
		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				if (RandUtil.nextInt(4) == 0) {
					Module module = ModuleRegistry.INSTANCE.getModule("effect_light");
					ParticleBuilder glitter = new ParticleBuilder(30);
					glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
					glitter.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
					glitter.setColorFunction(new InterpColorHSV(Color.CYAN, Color.BLUE));
					glitter.setScaleFunction(new InterpScale((float) RandUtil.nextDouble(1, 3), 0));
					ParticleSpawner.spawn(glitter, world, new StaticInterp<>(new Vec3d(pos).addVector(0.5, 0.5, 0.5)), 1, 0, (i, build) -> {
						build.setMotion(new Vec3d(
								RandUtil.nextDouble(-0.01, 0.01),
								RandUtil.nextDouble(0, 0.03),
								RandUtil.nextDouble(-0.01, 0.01)));

						if (RandUtil.nextBoolean()) {
							build.setColorFunction(new InterpColorHSV(module.getPrimaryColor(), module.getSecondaryColor()));
						} else {
							build.setColorFunction(new InterpColorHSV(module.getSecondaryColor(), module.getPrimaryColor()));
						}
					});
				}
			}
		});
	}
}
