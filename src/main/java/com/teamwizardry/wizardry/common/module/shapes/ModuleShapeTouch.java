package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpCircle;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.LOOK;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_touch")
public class ModuleShapeTouch implements IModuleShape {

	@Override
	public boolean run(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d look = spell.getData(LOOK);

		Entity caster = spell.getCaster();
		Vec3d origin = spell.getOrigin();

		if (look == null) return false;
		if (caster == null) return false;
		if (origin == null) return false;
		if (!spellRing.taxCaster(spell, true)) return false;

		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		overrides.onRunTouch(spell, spellRing);
		
		RayTraceResult result = new RayTrace(
				spell.world, look, origin,
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		spell.processTrace(result);
		return true;
	}

	@NotNull
	@Override
	public SpellData renderVisualization(ModuleInstanceShape instance, @Nonnull SpellData data, @Nonnull SpellRing ring, @Nonnull SpellData previousData) {
		Vec3d look = data.getData(LOOK);

		Entity caster = data.getCaster();
		Vec3d origin = data.getOrigin();

		if (look == null) return previousData;
		if (caster == null) return previousData;
		if (origin == null) return previousData;

		RayTraceResult result = new RayTrace(data.world, look, caster.getPositionVector().add(0, caster.getEyeHeight(), 0),
				caster instanceof EntityLivingBase ? ((EntityLivingBase) caster).getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() : 5)
				.setEntityFilter(input -> input != caster)
				.setReturnLastUncollidableBlock(true)
				.setIgnoreBlocksWithoutBoundingBoxes(true)
				.trace();

		data.processTrace(result);

		BlockPos pos = data.getTargetPos();
		if (pos == null) return previousData;

//		EnumFacing facing = result.sideHit;
//		IBlockState state = getCachableBlockstate(data.world, result.getBlockPos(), previousData);

		previousData.processTrace(result);

		return previousData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if( overrides.onRenderTouch(spell, spellRing) ) return;

		Entity targetEntity = spell.getVictim();

		if (targetEntity == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(targetEntity.getPositionVector().add(0, targetEntity.height / 2.0, 0), new Vec3d(0, 1, 0), 1, 10), 50, RandUtil.nextInt(10, 15), (aFloat, particleBuilder) -> {
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
	public void onRunTouch(SpellData data, SpellRing shape) {
		// Default implementation
	}
	
	@ModuleOverride("shape_touch_render")
	public boolean onRenderTouch(SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}

}
