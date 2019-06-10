package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.Easing;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.tesr.TileRenderHandler;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.tile.TileJar;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
public class TileJarRenderer extends TileRenderHandler<TileJar> {

	public Vec3d fairyPos = Vec3d.ZERO;
	private Animator ANIMATOR = new Animator();
	private TileJar te;

	public TileJarRenderer(TileJar jar) {
		super(jar);
		this.te = jar;
		animCurve();
	}

	@Override
	public void render(float partialTicks, int destroyStage, float alpha) {
		if (te.fairy == null) return;
		Vec3d pos = new Vec3d(te.getPos()).add(0.5, 0.35, 0.5).add(fairyPos);

		te.fairy.render(tile.getWorld(), pos, partialTicks);
	}

	private void animCurve() {
		if (te.fairy == null) {
			new BasicAnimation<>(this, "fairyPos").duration(20).completion(this::animCurve).addTo(ANIMATOR);
			return;
		}
		float excitement = (float) (1f - (tile.fairy.handler.getMana() / tile.fairy.handler.getMaxMana()) * (tile.fairy.isDepressed ? 0 : 1));
		new BasicAnimation<>(this, "fairyPos").ease(tile.fairy.isDepressed ? Easing.linear : Easing.easeInQuint)
				.to(new Vec3d(RandUtil.nextDouble(-0.1, 0.1), RandUtil.nextDouble(-0.25, 0.25), RandUtil.nextDouble(-0.1, 0.1)))
				.duration((RandUtil.nextInt(1, 5) + RandUtil.nextFloat(10, 20) * excitement + (tile.fairy.isDepressed ? RandUtil.nextFloat(100, 150) : 0)))
				.completion(this::animCurve).addTo(ANIMATOR);

	}
}
