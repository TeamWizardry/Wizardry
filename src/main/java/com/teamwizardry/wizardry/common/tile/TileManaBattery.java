package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpBezier3D;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.tesr.TileRenderer;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.block.IManaGenerator;
import com.teamwizardry.wizardry.api.block.TileManaNode;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaManager;
import com.teamwizardry.wizardry.api.util.ColorUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.render.block.TileManaBatteryRenderer;
import com.teamwizardry.wizardry.common.block.BlockManaBattery;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;

@TileRegister(Wizardry.MODID + ":mana_battery")
@TileRenderer(TileManaBatteryRenderer.class)
public class TileManaBattery extends TileManaNode implements IManaGenerator {

	public static final HashSet<BlockPos> poses = new HashSet<>();

	static {
		poses.add(new BlockPos(3, -1, 3));
		poses.add(new BlockPos(-3, -1, 3));
		poses.add(new BlockPos(3, -1, -3));
		poses.add(new BlockPos(-3, -1, -3));
	}

	@Save
	public boolean revealStructure = false;

	public TileManaBattery() {
		super(1000, 1000);
		setCanSuckFromOutside(false);
		setCanGiveToOutside(false);
	}

	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	public void update() {
		super.update();

		if (getBlockType() == ModBlocks.MANA_BATTERY && !((BlockManaBattery) getBlockType()).testStructure(getWorld(), getPos()).isEmpty())
			return;

		if (getStructurePos() != getPos()) {
			setStructurePos(getPos());
			markDirty();
		}

		if (getBlockType() != ModBlocks.CREATIVE_MANA_BATTERY) {
			for (BlockPos relative : poses) {
				BlockPos target = getPos().add(relative);
				TileEntity tile = world.getTileEntity(target);
				if (tile instanceof TileOrbHolder) {
					if (!((TileOrbHolder) tile).isPartOfStructure() || ((TileOrbHolder) tile).canSuckFromOutside() || !((TileOrbHolder) tile).canGiveToOutside()) {
						((TileOrbHolder) tile).setStructurePos(getPos());
						((TileOrbHolder) tile).setCanSuckFromOutside(false);
						((TileOrbHolder) tile).setCanGiveToOutside(true);
						tile.markDirty();
					}
				}
			}


			if (world.getTotalWorldTime() % 20 == 0 && !ManaManager.forObject(getWizardryCap()).isManaFull()) {
				ManaManager.forObject(getWizardryCap())
						.addMana(5)
						.removeBurnout(5)
						.close();

				if (world.isRemote)
					ClientRunnable.run(new ClientRunnable() {
						@Override
						@SideOnly(Side.CLIENT)
						public void runIfClient() {
							Vec3d from = new Vec3d(getPos()).add(0.5, 1, 0.5);
							Vec3d to = from.add(RandUtil.nextDouble(-1, 1), -3, RandUtil.nextDouble(-1, 1));

							ParticleBuilder helix = new ParticleBuilder(200);
							helix.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
							helix.setAlphaFunction(new InterpFloatInOut(0.1f, 0.1f));
							ParticleSpawner.spawn(helix, world, new StaticInterp<>(to), 5, 0, (someFloat, particleBuilder) -> {
								particleBuilder.setColor(ColorUtils.changeColorAlpha(new Color(0x0097FF), RandUtil.nextInt(50, 200)));
								particleBuilder.setScale(RandUtil.nextFloat(0.3f, 0.8f));
								particleBuilder.setPositionFunction(new InterpBezier3D(Vec3d.ZERO,
										from.subtract(to),
										new Vec3d(0, 5, 0), new Vec3d(0, 1, 0)));
								particleBuilder.setLifetime(RandUtil.nextInt(50, 60));
							});
						}
					});
			}

		} else {
			ManaManager.forObject(getWizardryCap())
					.setMana(ManaManager.getMaxMana(getWizardryCap()))
					.setBurnout(0)
					.close();
		}
	}
}
