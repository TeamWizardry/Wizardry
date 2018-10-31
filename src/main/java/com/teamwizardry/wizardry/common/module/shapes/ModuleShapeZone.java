package com.teamwizardry.wizardry.common.module.shapes;

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
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_zone")
public class ModuleShapeZone implements IModuleShape, ILingeringModule {

	public static final String ZONE_OFFSET = "zone offset";
	public static final String ZONE_CAST = "zone cast";
	
	@Override
	public String[] compatibleModifierClasses() {
		return new String[]{"modifier_increase_aoe", "modifier_increase_potency", "modifier_extend_range", "modifier_extend_time"};
	}

	@Override
	public boolean ignoreResultForRendering() {
		return true;
	}

	@Override
	public boolean run(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
//		Vec3d position = spell.getData(ORIGIN);
//		Entity caster = spell.getCaster();
		Vec3d targetPos = spell.getTargetWithFallback();

		if (targetPos == null) return false;

		double aoe = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);
		double potency = spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell);
		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);

		Vec3d min = targetPos.subtract(aoe/2, range/2, aoe/2);
		Vec3d max = targetPos.add(aoe / 2, range / 2, aoe / 2);

		NBTTagCompound info = spell.getData(SpellData.DefaultKeys.COMPOUND, new NBTTagCompound());

		double zoneOffset = info.getDouble(ZONE_OFFSET) + potency;
		info.setBoolean(ZONE_CAST, false);
		if (zoneOffset >= ConfigValues.zoneTimer)
		{
			zoneOffset %= ConfigValues.zoneTimer;
			if (!spellRing.taxCaster(spell, true))
			{
				info.setDouble(ZONE_OFFSET, zoneOffset);
				spell.addData(COMPOUND, info);
				return false;
			}
			
			IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
//			instance.runRunOverrides(spell, spellRing);
			overrides.onRunZone(spell, spellRing);
			
			BlockPos target = new BlockPos(RandUtil.nextDouble(min.x, max.x), RandUtil.nextDouble(min.y, max.y), RandUtil.nextDouble(min.z, max.z));
			List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target));
			for (Entity entity : entities)
			{
				Vec3d vec = new Vec3d(RandUtil.nextDouble(min.x, max.x), RandUtil.nextDouble(min.y, max.y), RandUtil.nextDouble(min.z, max.z));

				SpellData copy = spell.copy();
				copy.processEntity(entity, false);
				copy.addData(YAW, entity.rotationYaw);
				copy.addData(PITCH, entity.rotationPitch);
				copy.addData(ORIGIN, vec);

				if (spellRing.getChildRing() != null)
					spellRing.getChildRing().runSpellRing(spell);
			}
			Vec3d pos = new Vec3d(target).add(0.5, 0.5, 0.5);
				
			SpellData copy = spell.copy();
			copy.addData(ORIGIN, pos);
			copy.processBlock(target, EnumFacing.UP, pos);
			copy.addData(YAW, RandUtil.nextFloat(-180, 180));
			copy.addData(PITCH, RandUtil.nextFloat(-50, 50));

			if (spellRing.getChildRing() != null)
				spellRing.getChildRing().runSpellRing(copy);
		}
		info.setDouble(ZONE_OFFSET, zoneOffset);

		spell.addData(COMPOUND, info);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if( overrides.onRenderZone(spell, spellRing) ) return;
//		if (instance.runRenderOverrides(spell, spellRing)) return;

		Vec3d target = spell.getTarget();

		if (target == null) return;
		if (RandUtil.nextInt(10) != 0) return;

		double aoe = spellRing.getAttributeValue(AttributeRegistry.AREA, spell);

		ParticleBuilder glitter = new ParticleBuilder(10);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setCollision(true);
		ParticleSpawner.spawn(glitter, spell.world, new InterpCircle(target, new Vec3d(0, 1, 0), (float) aoe, 1, RandUtil.nextFloat()), (int) (aoe * 25), 10, (aFloat, particleBuilder) -> {
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
	public int getLingeringTime(SpellData spell, SpellRing spellRing) {
		return (int) spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;
	}
	
	////////////////
	
	@ModuleOverride("shape_zone_run")
	public void onRunZone(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		// Default implementation
	}

	@ModuleOverride("shape_zone_render")
	public boolean onRenderZone(SpellData data, SpellRing shape, @ContextRing SpellRing childRing) {
		// Default implementation
		return false;
	}
}
