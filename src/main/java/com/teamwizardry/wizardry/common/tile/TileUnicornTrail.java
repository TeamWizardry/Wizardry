package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.teamwizardry.wizardry.api.Constants.MISC.SPARKLE_BLURRED;

/**
 * Created by Demoniaque.
 */
@TileRegister("unicorn_trail")
public class TileUnicornTrail extends TileMod implements ITickable {

	@Save
	public long savedTime = System.currentTimeMillis();

	@Override
	public void update() {
		RandUtilSeed seed = new RandUtilSeed(getPos().toLong());
		if (System.currentTimeMillis() - savedTime >= seed.nextInt(3000, 5000)) {
			getWorld().setBlockToAir(getPos());
		} else {
			if (RandUtil.nextInt(20) == 0)
				ClientRunnable.run(new ClientRunnable() {
					@Override
					@SideOnly(Side.CLIENT)
					public void runIfClient() {
						ParticleBuilder builder = new ParticleBuilder(50);
						builder.setRender(new ResourceLocation(Wizardry.MODID, SPARKLE_BLURRED));
						builder.disableMotionCalculation();
						builder.setAlphaFunction(new InterpFadeInOut(0.3f, 0f));
						builder.setScaleFunction(new InterpScale(0.3f, 0f));
						ParticleSpawner.spawn(builder, world, new StaticInterp<>(new Vec3d(getPos()).addVector(0.5, 0.5, 0.5)), 2, 0, (aFloat, particleBuilder) -> {
							particleBuilder.setPositionOffset(new Vec3d(
									RandUtil.nextDouble(-0.5, 0.5),
									RandUtil.nextDouble(-0.5, 0.5),
									RandUtil.nextDouble(-0.5, 0.5)
							));
							particleBuilder.setMotion(new Vec3d(
									RandUtil.nextDouble(-0.1, 0.1),
									RandUtil.nextDouble(-0.1, 0.1),
									RandUtil.nextDouble(-0.1, 0.1)
							));
						});
					}
				});
		}
	}
}
