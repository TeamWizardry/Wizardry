package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class TileCraftingPlate extends TileEntity implements ITickableTileEntity {

	public TileCraftingPlate() {
		super(LibTileEntityType.CRAFTING_PLATE);
	}

	@Override
	public void tick() {

		if (world.isRemote && world.getGameTime() % 3 == 0)
			Wizardry.PROXY.spawnParticle(
					new GlitterBox.GlitterBoxFactory()
							.setOrigin(new Vec3d(getPos()).add(0, 1, 0))
							.setTarget(new Vec3d(RandUtil.nextDouble(-1, 1), RandUtil.nextDouble(0, 1), RandUtil.nextDouble(-1, 1)))
							.setGravity(0.1f)
							.setBounce(0.5f)
							.setDrag(0.05f)
							.setFriction(0.15f)
							.setInitialColor(Color.CYAN)
							.setInitialSize(0.3f)
							.createGlitterBox(200));
	}
}
