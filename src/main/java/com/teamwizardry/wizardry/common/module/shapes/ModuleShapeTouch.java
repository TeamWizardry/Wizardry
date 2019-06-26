package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.RenderUtils;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "shape_touch")
public class ModuleShapeTouch implements IModuleShape {

	@Override
	public boolean run(@NotNull World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d look = spell.getData(LOOK);

		Entity caster = spell.getCaster(world);
		Vec3d origin = spell.getOrigin(world);

		if (look == null) return false;
		if (caster == null) return false;
		if (origin == null) return false;
		if (!spellRing.taxCaster(world, spell, true)) return false;

		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		overrides.onRunTouch(world, spell, spellRing);

		RayTraceResult result = new RayTrace(
				world, look, origin,
				caster instanceof EntityLivingBase && ((EntityLivingBase) caster).getAttributeMap().getAllAttributes().contains(EntityPlayer.REACH_DISTANCE) ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		spell.processTrace(result);
		return true;
	}

	@NotNull
	@Override
	public SpellData renderVisualization(@Nonnull World world, ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, float partialTicks) {
		Vec3d look = data.getData(LOOK);

		Entity caster = data.getCaster(world);
		Vec3d origin = data.getOrigin(world);

		if (look == null) return data;
		if (caster == null) return data;
		if (origin == null) return data;

		double interpPosX = caster.lastTickPosX + (caster.posX - caster.lastTickPosX) * partialTicks;
		double interpPosY = caster.lastTickPosY + (caster.posY - caster.lastTickPosY) * partialTicks;
		double interpPosZ = caster.lastTickPosZ + (caster.posZ - caster.lastTickPosZ) * partialTicks;

		RayTraceResult result = new RayTrace(world, look, new Vec3d(interpPosX, interpPosY + caster.getEyeHeight(), interpPosZ),
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		BlockPos pos = data.getTargetPos();
		EnumFacing facing = data.getFaceHit();
		Vec3d target = data.getTarget(world);
		if (pos == null) return data;

//		EnumFacing facing = result.sideHit;
//		IBlockState state = getCachableBlockstate(world, result.getBlockPos(), data);

		if (facing != null && !world.isAirBlock(pos))
			RenderUtils.drawFaceOutline(pos, facing);
		else if (target != null) {
			RenderUtils.drawCircle(target, 0.3, true, false);
		}

		data.processTrace(result);

		return data;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if (overrides.onRenderTouch(world, spell, spellRing)) return;

		Entity targetEntity = spell.getVictim(world);

		if (targetEntity == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(glitter, world, new InterpCircle(targetEntity.getPositionVector().add(0, targetEntity.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 10), 50, RandUtil.nextInt(10, 15), (aFloat, particleBuilder) -> {
			if (RandUtil.nextBoolean()) {
				glitter.setColor(spellRing.getPrimaryColor());
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(0.01, 0.1), 0));
			} else {
				glitter.setColor(spellRing.getSecondaryColor());
				glitter.setMotion(new Vec3d(0, RandUtil.nextDouble(-0.1, -0.01), 0));
			}
			glitter.setLifetime(RandUtil.nextInt(20, 30));
			glitter.setScale((float) RandUtil.nextDouble(0.3, 1));
			glitter.setAlphaFunction(new InterpFloatInOut(0.3f, (float) RandUtil.nextDouble(0.6, 1)));
			glitter.setLifetime(RandUtil.nextInt(10, 20));
			glitter.setScaleFunction(new InterpScale(1, 0));
		});
	}

	//////////////////

	@ModuleOverride("shape_touch_run")
	public void onRunTouch(World world, SpellData data, SpellRing shape) {
		// Default implementation
	}

	@ModuleOverride("shape_touch_render")
	public boolean onRenderTouch(World world, SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}

}
