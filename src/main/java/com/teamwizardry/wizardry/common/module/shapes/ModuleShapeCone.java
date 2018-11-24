package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.position.InterpLine;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RayTrace;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;


/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_cone")
public class ModuleShapeCone implements IModuleShape {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_potency", "modifier_extend_range"};
	}

	@Override
	public boolean ignoreResultForRendering() {
		return true;
	}

	@Override
	public boolean run(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Entity caster = spell.getCaster();

		Vec3d origin = spell.getOriginHand();
		if (origin == null) return false;
		
		double range = spellRing.getAttributeValue(AttributeRegistry.RANGE, spell);
		int potency = (int) (spellRing.getAttributeValue(AttributeRegistry.POTENCY, spell));

		for (int i = 0; i < potency; i++) {

			if (!spellRing.taxCaster(spell, 1.0 / potency, true)) return false;
			
			long seed = RandUtil.nextLong(100, 10000);
			spell.addData(SEED, seed);
			
			IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
			overrides.onRunCone(spell, spellRing);
			
			float angle = (float) range * 2;
			float newPitch = pitch + RandUtil.nextFloat(-angle, angle);
			float newYaw = yaw + RandUtil.nextFloat(-angle, angle);

			Vec3d target = PosUtils.vecFromRotations(newPitch, newYaw);

			SpellData newSpell = spell.copy();

			RayTraceResult result = new RayTrace(world, target.normalize(), origin, range)
					.setEntityFilter(input -> input != caster)
					.trace();

			Vec3d lookFallback = spell.getData(LOOK);
			if (lookFallback != null) lookFallback.scale(range);
			newSpell.processTrace(result, lookFallback);

			instance.sendRenderPacket(newSpell, spellRing);

			newSpell.addData(ORIGIN, result.hitVec);

			if (spellRing.getChildRing() != null) {
				spellRing.getChildRing().runSpellRing(newSpell.copy());
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		IShapeOverrides overrides = spellRing.getOverrideHandler().getConsumerInterface(IShapeOverrides.class);
		if( overrides.onRenderCone(spell, spellRing) ) return;

		Vec3d target = spell.getTarget();

		if (target == null) return;

		Vec3d origin = spell.getOriginHand();
		if (origin == null) return;

		ParticleBuilder lines = new ParticleBuilder(10);
		lines.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		lines.setScaleFunction(new InterpScale(0.5f, 0));
		lines.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
		ParticleSpawner.spawn(lines, spell.world, new InterpLine(origin, target), (int) target.distanceTo(origin) * 4, 0, (aFloat, particleBuilder) -> {
			lines.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
			lines.setLifetime(RandUtil.nextInt(10, 20));
		});
	}
	
	/////////////
	
	@ModuleOverride("shape_cone_run")
	public void onRunCone(SpellData data, SpellRing shape) {
		// Default implementation
	}
	
	@ModuleOverride("shape_cone_render")
	public boolean onRenderCone(SpellData data, SpellRing shape) {
		// Default implementation
		return false;
	}
}
