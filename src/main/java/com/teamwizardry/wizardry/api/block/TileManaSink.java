package com.teamwizardry.wizardry.api.block;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.EAST;
import static net.minecraft.util.EnumFacing.NORTH;
import static net.minecraft.util.EnumFacing.SOUTH;
import static net.minecraft.util.EnumFacing.UP;
import static net.minecraft.util.EnumFacing.WEST;

import java.awt.Color;

import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.saving.CapabilityProvide;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;

import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad.
 */
public class TileManaSink extends TileMod implements ITickable {

	@Save
	public BlockPos faucetPos;

	@Save
	@CapabilityProvide(sides = {DOWN, UP, NORTH, SOUTH, WEST, EAST})
	public CustomWizardryCapability cap;

	public TileManaSink(double maxMana, double maxBurnout) {
		cap = new CustomWizardryCapability(maxMana, maxBurnout);
	}

	@Override
	public void update() {
		if (faucetPos != null) {
			CapManager sink = new CapManager(cap);

			TileManaFaucet tileFaucet = (TileManaFaucet) world.getTileEntity(faucetPos);
			if (tileFaucet == null) return;

			CapManager faucet = new CapManager(tileFaucet.cap);

			if (sink.isManaFull()) return;

			double idealAmount = 100;
			if (faucet.getMana() < idealAmount) return;
			faucet.removeMana(idealAmount);
			sink.addMana(idealAmount);

			ClientRunnable.run(new ClientRunnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void runIfClient()
				{
					ParticleBuilder helix = new ParticleBuilder(200);
					helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
					helix.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));

					ParticleSpawner.spawn(helix, world, new StaticInterp<>(new Vec3d(faucetPos).addVector(0.5, 1, 0.5)), 1, 0, (aFloat, particleBuilder) -> {
						helix.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
						helix.setScale(RandUtil.nextFloat());
						helix.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(pos.subtract(faucetPos)), new Vec3d(0, 20, 0), new Vec3d(0, 5, 0)));
						helix.setLifetime(RandUtil.nextInt(10, 40));
					});
				}
			});

		}
	}

	protected final boolean consumeMana(double amount) {
		CapManager manager = new CapManager(cap);

		if (amount <= manager.getMana()) {
			manager.removeMana(amount);
			return true;
		}
		return false;
	}
}
