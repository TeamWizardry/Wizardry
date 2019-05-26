package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;


/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "shape_zone")
public class ModuleShapeZone implements IModuleShape, ILingeringModule {

	public static final String ZONE_OFFSET = "zone offset";
	public static final String ZONE_CAST = "zone cast";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe", "modifier_increase_potency", "modifier_extend_range", "modifier_extend_time"};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean ignoreResultsForRendering() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRunChildren() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean run(@NotNull World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		//		Vec3d position = spell.getData(ORIGIN);
//		Entity caster = spell.getCaster(world);
		Vec3d targetPos = spell.getTargetWithFallback(world);

		if (targetPos == null) return false;

		double aoe = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);
		double potency = spellRing.getAttributeValue(world, AttributeRegistry.POTENCY, spell);
		double range = spellRing.getAttributeValue(world, AttributeRegistry.RANGE, spell);

		Vec3d min = targetPos.subtract(aoe / 2, range / 2, aoe / 2);
		Vec3d max = targetPos.add(aoe / 2, range / 2, aoe / 2);

		NBTTagCompound info = spell.getDataWithFallback(SpellData.DefaultKeys.COMPOUND, new NBTTagCompound());

		double zoneOffset = info.getDouble(ZONE_OFFSET) + potency;
		info.setBoolean(ZONE_CAST, false);
		if (zoneOffset >= ConfigValues.zoneTimer) {
			zoneOffset %= ConfigValues.zoneTimer;
			if (!spellRing.taxCaster(world, spell, true)) {
				info.setDouble(ZONE_OFFSET, zoneOffset);
				spell.addData(COMPOUND, info);
				return false;
			}

			IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
			overrides.onRunZone(world, spell, spellRing);

			BlockPos target = new BlockPos(RandUtil.nextDouble(min.x, max.x), RandUtil.nextDouble(min.y, max.y), RandUtil.nextDouble(min.z, max.z));
			List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target));
			for (Entity entity : entities) {
				Vec3d vec = new Vec3d(RandUtil.nextDouble(min.x, max.x), RandUtil.nextDouble(min.y, max.y), RandUtil.nextDouble(min.z, max.z));

				SpellData copy = spell.copy();
				copy.processEntity(entity, false);
				copy.addData(YAW, entity.rotationYaw);
				copy.addData(PITCH, entity.rotationPitch);
				copy.addData(ORIGIN, vec);

				if (spellRing.getChildRing() != null)
					spellRing.getChildRing().runSpellRing(world, spell, true);
			}
			Vec3d pos = new Vec3d(target).add(0.5, 0.5, 0.5);

			SpellData copy = spell.copy();
			copy.addData(ORIGIN, pos);
			copy.processBlock(target, EnumFacing.UP, pos);
			copy.addData(YAW, RandUtil.nextFloat(-180, 180));
			copy.addData(PITCH, RandUtil.nextFloat(-50, 50));

			if (spellRing.getChildRing() != null)
				spellRing.getChildRing().runSpellRing(world, copy, true);
		}
		info.setDouble(ZONE_OFFSET, zoneOffset);

		spell.addData(COMPOUND, info);
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		Vec3d look = data.getData(SpellData.DefaultKeys.LOOK);
		Vec3d target = data.getTarget(world);
		Entity caster = data.getCaster(world);

		if (caster == null) return previousData;
		if (target == null) {
			if (look == null) return previousData;

			RayTraceResult result = new RayTrace(world, look, caster.getPositionVector().add(0, caster.getEyeHeight(), 0),
					caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
					.setEntityFilter(input -> input != caster)
					.setReturnLastUncollidableBlock(true)
					.setIgnoreBlocksWithoutBoundingBoxes(true)
					.trace();

			data.processTrace(result);

			BlockPos pos = data.getTargetPos();
			if (pos == null) return previousData;

			previousData.processTrace(result);

			target = data.getTarget(world);
		}
		if (target == null) return previousData;

		double aoe = ring.getAttributeValue(world, AttributeRegistry.AREA, data);

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

		int color = Color.HSBtoRGB(ClientTickHandler.getTicks() % 200 / 200F, 0.6F, 1F);
		Color colorRGB = new Color(color);

		GL11.glLineWidth(2f);
		GL11.glColor4ub((byte) colorRGB.getRed(), (byte) colorRGB.getGreen(), (byte) colorRGB.getBlue(), (byte) 255);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bb = tessellator.getBuffer();
		bb.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
		for (int i = 0; i <= 360; i++) {
			double x = target.x + aoe * MathHelper.cos((float) ((i / 360.0) * Math.PI * 2));
			double z = target.z + aoe * MathHelper.sin((float) ((i / 360.0) * Math.PI * 2));
			double y = target.y;
			bb.pos(x, y, z).endVertex();
		}
		tessellator.draw();

		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();

		GlStateManager.enableDepth();
		GlStateManager.popMatrix();

		return previousData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if (overrides.onRenderZone(world, spell, spellRing)) return;

		Vec3d target = spell.getTarget(world);

		if (target == null) return;
		if (RandUtil.nextInt(10) != 0) return;

		double aoe = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell);

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, world, new InterpCircle(target, new Vec3d(0, 1, 0), (float) aoe, 1, RandUtil.nextFloat()), (int) (aoe * 25), 10, (aFloat, particleBuilder) -> {
			glitter.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
			glitter.setLifetime(RandUtil.nextInt(30, 50));
			glitter.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
			glitter.setMotion(new Vec3d(
					RandUtil.nextDouble(-0.01, 0.01),
					RandUtil.nextDouble(-0.1, 0.1),
					RandUtil.nextDouble(-0.01, 0.01)
			));
		});
	}

	@Override
	public int getLingeringTime(World world, SpellData spell, SpellRing spellRing) {
		return (int) spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
	}

	////////////////

	@ModuleOverride("shape_zone_run")
	public void onRunZone(World world, SpellData data, SpellRing shape) {
		// Default implementation
	}

	@ModuleOverride("shape_zone_render")
	public boolean onRenderZone(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}
}
