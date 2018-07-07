package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleShape;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.common.entity.projectile.EntitySpellProjectile;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseRange;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseSpeed;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
public class ModuleShapeProjectile extends ModuleShape {

	@Nonnull
	@Override
	public String getID() {
		return "shape_projectile";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseRange(), new ModuleModifierIncreaseSpeed()};
	}

	@Override
	public boolean ignoreResultForRendering() {
		return true;
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		if (world.isRemote) return true;

		Vec3d origin = spell.getOriginWithFallback();
		if (origin == null) return false;

		double dist = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		double speed = spellRing.getAttributeValue(AttributeRegistry.SPEED, spell);

		EntitySpellProjectile proj = new EntitySpellProjectile(world, spellRing, spell, (float) dist, (float) speed, (float) 0.1);
		proj.setPosition(origin.x, origin.y, origin.z);
		proj.velocityChanged = true;

		if (!spellRing.taxCaster(spell)) return false;

		boolean success = world.spawnEntity(proj);
		if (success)
			world.playSound(null, new BlockPos(origin), ModSounds.PROJECTILE_LAUNCH, SoundCategory.PLAYERS, 1f, (float) RandUtil.nextDouble(1, 1.5));
		return success;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (spellRing.isRunBeingOverriden()) {
			runRenderOverrides(spell, spellRing);
		}
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

		double dist = ring.getAttributeValue(AttributeRegistry.RANGE, data);

		RayTraceResult result = new RayTrace(
				data.world, look, caster.getPositionVector().addVector(0, caster.getEyeHeight(), 0), dist)
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
}
