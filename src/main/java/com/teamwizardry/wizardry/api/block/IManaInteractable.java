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
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashSet;

public interface IManaInteractable {

	@Nullable
	IWizardryCapability getCap();

	default boolean suckManaFrom(World world, BlockPos origin, IWizardryCapability capToFill, BlockPos from, double idealAmount, boolean equalize) {
		if (!world.isBlockLoaded(from)) return false;
		if (capToFill == null) return false;
		if (capToFill.getMaxMana() <= capToFill.getMana()) return false;
		if (new CapManager(capToFill).isManaFull()) return false;
		if (origin.getDistance(from.getX(), from.getY(), from.getZ()) > ConfigValues.networkLinkDistance)
			return false;

		TileEntity tile = world.getTileEntity(from);
		if (tile != null && tile instanceof TileManaInteracter) {

			if (equalize) {
				IWizardryCapability cap = ((TileManaInteracter) tile).getCap();
				if (cap != null && capToFill.getMana() + idealAmount > cap.getMana() - idealAmount) {
					return false;
				}
			}

			double amount = ((TileManaInteracter) tile).drainMana(world, from, ((TileManaInteracter) tile).getCap(), idealAmount);
			if (amount <= 0) return false;

			CapManager manager = new CapManager(capToFill);
			manager.addMana(amount);

			if (RandUtil.nextInt(5) == 0)
				ClientRunnable.run(new ClientRunnable() {
					@Override
					@SideOnly(Side.CLIENT)
					public void runIfClient() {
						ParticleBuilder helix = new ParticleBuilder(200);
						helix.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
						helix.setAlphaFunction(new InterpFadeInOut(0.1f, 0.1f));
						ParticleSpawner.spawn(helix, world, new StaticInterp<>(new Vec3d(from).addVector(0.5, 1, 0.5)), 1, 0, (someFloat, particleBuilder) -> {
							particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
							particleBuilder.setScale(RandUtil.nextFloat(0.5f, 0.8f));
							particleBuilder.setPositionFunction(new InterpBezier3D(Vec3d.ZERO, new Vec3d(origin.subtract(from)), new Vec3d(0, 5, 0), new Vec3d(0, -5, 0)));
							particleBuilder.setLifetime(RandUtil.nextInt(50, 60));
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

	default HashSet<BlockPos> getNearestSuckables(Class<? extends TileManaInteracter> clazz, World world, BlockPos origin, boolean ignoreStorage) {
		HashSet<BlockPos> poses = new HashSet<>();
		HashSet<BlockPos> temp = new HashSet<>(TileManaInteracter.MANA_INTERACTABLES.asMap().keySet());
		for (BlockPos target : temp) {
			if (target.equals(origin)) continue;
			if (!world.isBlockLoaded(target)) continue;
			if (target.getDistance(origin.getX(), origin.getY(), origin.getZ()) > ConfigValues.networkLinkDistance)
				continue;

			TileEntity tile = world.getTileEntity(target);
			if (tile == null || !(tile.getClass().isAssignableFrom(clazz))) continue;

			if (!ignoreStorage) {
				if (((TileManaInteracter) tile).getCap() == null) continue;
				CapManager manager = new CapManager(((TileManaInteracter) tile).getCap());
				if (manager.isManaEmpty()) continue;
			}
			poses.add(target);
		}
		return poses;
	}
}
