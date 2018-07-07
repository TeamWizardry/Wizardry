package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;
import static com.teamwizardry.wizardry.api.util.PosUtils.getPerpendicularFacings;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleShapeTouch extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_touch";
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (runRunOverrides(spell, spellRing)) return true;

		Vec3d look = spell.getData(LOOK);

		Entity caster = spell.getCaster();
		Vec3d origin = spell.getOrigin();

		if (look == null) return false;
		if (caster == null) return false;
		if (origin == null) return false;
		if (!spellRing.taxCaster(spell)) return false;

		RayTraceResult result = new RayTrace(
				spell.world, look, origin,
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setSkipEntity(caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		spell.processTrace(result);
		return true;
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		Vec3d look = data.getData(LOOK);

		Entity caster = data.getCaster();
		Vec3d origin = data.getOrigin();

		if (look == null) return previousData;
		if (caster == null) return previousData;
		if (origin == null) return previousData;

		RayTraceResult result = new RayTrace(data.world, look, caster.getPositionVector().addVector(0, caster.getEyeHeight(), 0),
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setSkipEntity(caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		BlockPos pos = data.getTargetPos();
		if (pos == null) return previousData;

		EnumFacing facing = result.sideHit;
		IBlockState state = getCachableBlockstate(data.world, result.getBlockPos(), previousData);

		previousData.processTrace(result);

		GlStateManager.pushMatrix();

		GlStateManager.disableDepth();
		GlStateManager.disableCull();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableTexture2D();
		GlStateManager.enableColorMaterial();

		int color = Color.HSBtoRGB(ClientTickHandler.getTicks() % 100 / 100F, 0.6F, 1F);
		Color colorRGB = new Color(color);

		GL11.glLineWidth(1f);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 255);

		if (BlockUtils.isAnyAir(state)) {

			RenderUtils.renderBlockOutline(state.getSelectedBoundingBox(data.world, pos));

		} else {

			Tessellator tessellator = Tessellator.getInstance();
			tessellator.getBuffer().begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

			Vec3d directionOffsetVec = new Vec3d(facing.getDirectionVec()).scale(0.5);
			Vec3d adjPos = new Vec3d(pos).addVector(0.5, 0.5, 0.5).add(directionOffsetVec);

			for (EnumFacing facing1 : getPerpendicularFacings(facing)) {
				for (EnumFacing facing2 : getPerpendicularFacings(facing)) {
					if (facing1 == facing2 || facing1.getOpposite() == facing2 || facing2.getOpposite() == facing1)
						continue;

					Vec3d p1 = new Vec3d(facing1.getDirectionVec()).scale(0.5);
					Vec3d p2 = new Vec3d(facing2.getDirectionVec()).scale(0.5);
					Vec3d edge = adjPos.add(p1.add(p2));

					tessellator.getBuffer().pos(edge.x, edge.y, edge.z).endVertex();
				}
			}

			tessellator.draw();
		}

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();

		GlStateManager.enableDepth();
		GlStateManager.popMatrix();

		return previousData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (runRenderOverrides(spell, spellRing)) return;

		Entity targetEntity = spell.getVictim();

		if (targetEntity == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(targetEntity.getPositionVector().addVector(0, targetEntity.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 10), 50, RandUtil.nextInt(10, 15), (aFloat, particleBuilder) -> {
			if (RandUtil.nextBoolean()) {
				glitter.setColor(spellRing.getPrimaryColor());
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.1), 0));
			} else {
				glitter.setColor(spellRing.getSecondaryColor());
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(-0.1, -0.01), 0));
			}
			glitter.setLifetime(RandUtil.nextInt(20, 30));
			glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
			glitter.setLifetime(RandUtil.nextInt(10, 20));
			glitter.setScaleFunction(new InterpScale(1, 0));
		});
	}
}
