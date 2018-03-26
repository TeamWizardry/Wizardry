package com.teamwizardry.wizardry.client.render.block;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.common.tile.TileJar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
public class TileJarRenderer extends TileEntitySpecialRenderer<TileJar> {

	private IBakedModel modelJar;

	public TileJarRenderer() {
		ClientRunnable.registerReloadHandler(() -> modelJar = null);
	}

	@SuppressWarnings("unused")
	private void getBakedModels() {
		IModel model = null;
		if (modelJar == null) {
			try {
				model = ModelLoaderRegistry.getModel(new ResourceLocation(Wizardry.MODID, "block/jar"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			modelJar = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
		}
	}


	@Override
	public void render(TileJar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		//InterpBezier3D bezier = new InterpBezier3D(new Vec3d(te.getPos().add(-5, 5, 0)), new Vec3d(te.getPos().add(5, 5, 0)), new Vec3d(0, 3, 0), new Vec3d(0, -3, 0));
		////InterpLine bezier = new InterpLine(new Vec3d(te.getPos().add(-5, 5, 0)), new Vec3d(te.getPos().add(5, 5, 0)));
		//LightEffectUtil.renderBilinearGradient(bezier.list(50), Color.WHITE, 0.2, new Vec3d(0, 1, 0));
		//LightEffectUtil.renderBilinearGradient(bezier.list(50), Color.BLUE, 0.3, new Vec3d(0, 1, 0));
		//LightEffectUtil.renderBilinearGradient(bezier.list(50), Color.CYAN, 0.5, new Vec3d(0, 1, 0));
		//LightEffectUtil.renderBilinearGradient(bezier.list(50), Color.CYAN, 1.5, new Vec3d(0, 1, 0));
		if (!te.hasFairy) return;
		double timeDifference = (ClientTickHandler.getTicks() + partialTicks) / 20.0;
		Vec3d pos = new Vec3d(te.getPos()).addVector(0.5, 0.35 + 0.2 * MathHelper.sin((float) timeDifference), 0.5);

		Color color = te.color;
		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setColor(color);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));
		glitter.setScale(0.3f);
		ParticleSpawner.spawn(glitter, te.getWorld(), new StaticInterp<>(pos), 1);

		if (RandUtil.nextInt(10) == 0) {
			ParticleBuilder trail = new ParticleBuilder(20);
			trail.setColor(te.color);
			trail.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
			trail.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));
			trail.setScale(0.2f);
			//trail.enableMotionCalculation();
			ParticleSpawner.spawn(trail, te.getWorld(), new StaticInterp<>(pos), 1, 0, (aFloat, particleBuilder) -> {
				trail.setMotion(new Vec3d(
						RandUtil.nextDouble(-0.005, 0.005),
						RandUtil.nextDouble(-0.005, 0.005),
						RandUtil.nextDouble(-0.005, 0.005)
				));
			});
		}
	}
}
