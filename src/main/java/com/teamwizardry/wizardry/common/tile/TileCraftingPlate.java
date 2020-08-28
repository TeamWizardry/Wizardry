package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.math.Vec2d;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.utils.MathUtils;
import com.teamwizardry.wizardry.api.utils.RandUtil;
import com.teamwizardry.wizardry.client.lib.LibTheme;
import com.teamwizardry.wizardry.client.particle.GlitterBox;
import com.teamwizardry.wizardry.common.lib.LibTileEntityType;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;

public class TileCraftingPlate extends TileEntity implements ITickableTileEntity {

	public TileCraftingPlate() {
		super(LibTileEntityType.CRAFTING_PLATE);
	}

	@Override
	public void tick() {

		if (world != null && world.isRemote && world.getGameTime() % 3 == 0) {

			Vec3d target = new Vec3d(RandUtil.nextDouble(-0.01, 0.01),
					RandUtil.nextDouble(0, 0.05),
					RandUtil.nextDouble(-0.01, 0.01));
			for (int i = 0; i < 5; i++) {
				Vec2d randDot = MathUtils.genRandomDotInCircle(0.1f);
				Vec3d origin = new Vec3d(getPos()).add(0.5 + randDot.getX(), 0.7 + RandUtil.nextDouble(0, 0.3), 0.5 + randDot.getY());
				Wizardry.PROXY.spawnParticle(
						new GlitterBox.GlitterBoxFactory()
								.setOrigin(origin)
								.setTarget(target)
								.setDrag(RandUtil.nextFloat(0.03f, 0.05f))
								.setGoalColor(LibTheme.accentColor)
								.setInitialSize(RandUtil.nextFloat(0.1f, 0.2f))
								.setGoalSize(0)
								.createGlitterBox(RandUtil.nextInt(5, 30)));
			}
		}
	}
}
