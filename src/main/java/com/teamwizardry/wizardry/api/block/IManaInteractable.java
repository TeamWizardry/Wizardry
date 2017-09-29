package com.teamwizardry.wizardry.api.block;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashSet;

public interface IManaInteractable {

	@Nullable
	IWizardryCapability getCap();

	default boolean suckManaFrom(World world, BlockPos origin, @Nullable IWizardryCapability cap, BlockPos from, double idealAmount, boolean equalize) {
		if (!world.isBlockLoaded(from)) return false;
		if (cap == null) return false;
		if (cap.getMaxMana() <= cap.getMana()) return false;
		if (new CapManager(cap).isManaFull()) return false;
		if (origin.getDistance(from.getX(), from.getY(), from.getZ()) > ConfigValues.manaBatteryLinkDistance)
			return false;

		TileEntity tile = world.getTileEntity(from);
		if (tile != null && tile instanceof TileManaInteracter) {

			double amount = ((TileManaInteracter) tile).drainMana(world, from, ((TileManaInteracter) tile).getCap(), idealAmount);
			if (amount < 0) return false;

			CapManager manager = new CapManager(cap);
			manager.addMana(amount);

			if (RandUtil.nextInt(2) == 0)
				ClientRunnable.run(new ClientRunnable() {
					@Override
					@SideOnly(Side.CLIENT)
					public void runIfClient() {
						ParticleBuilder helix = new ParticleBuilder(200);
						helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
						helix.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));

						ParticleSpawner.spawn(helix, world, new StaticInterp<>(new Vec3d(from).addVector(0.5, 1, 0.5)), 1, 0, (aFloat, particleBuilder) -> {
							particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
							particleBuilder.setScale(RandUtil.nextFloat());
							//particleBuilder.setPositionOffset(new Vec3d(RandUtil.nextDouble(-0.1, 0.1), RandUtil.nextDouble(-0.1, 0.1), RandUtil.nextDouble(-0.1, 0.1)));
							particleBuilder.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(origin.subtract(from)), new Vec3d(0, 5, 0), new Vec3d(0, -5, 0)));
							particleBuilder.setLifetime(RandUtil.nextInt(30, 50));
						});
					}
				});
			return true;
		}
		return false;
	}

	default double drainMana(World world, BlockPos pos, @Nullable IWizardryCapability cap, double mana) {
		if (!world.isBlockLoaded(pos)) return -1;
		if (cap == null) return -1;
		double amount = cap.getMana() < mana ? cap.getMana() : mana;
		CapManager manager = new CapManager(cap);
		manager.removeMana(amount);
		return amount;
	}

	@NotNull
	default HashSet<BlockPos> getNearestSuckables(Class<? extends TileManaInteracter> clazz, World world, BlockPos origin) {
		HashSet<BlockPos> poses = new HashSet<>();
		HashSet<BlockPos> temp = new HashSet<>(TileManaInteracter.MANA_INTERACTABLES.asMap().keySet());
		for (BlockPos target : temp) {
			if (target.equals(origin)) continue;
			if (!world.isBlockLoaded(target)) continue;
			if (target.getDistance(origin.getX(), origin.getY(), origin.getZ()) > ConfigValues.manaBatteryLinkDistance)
				continue;

			TileEntity tile = world.getTileEntity(target);
			if (tile == null || !(tile.getClass().isAssignableFrom(clazz))) continue;
			if (((TileManaInteracter) tile).getCap() == null) continue;
			CapManager manager = new CapManager(((TileManaInteracter) tile).getCap());
			if (manager.isManaEmpty()) continue;
			poses.add(target);
		}
		return poses;
	}
}
