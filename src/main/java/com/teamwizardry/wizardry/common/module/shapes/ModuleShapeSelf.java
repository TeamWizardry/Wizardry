package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleParameter;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.module.IModuleShape;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceShape;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="shape_self")
public class ModuleShapeSelf implements IModuleShape {

	enum TestEnum {
		BLA, BLUB
	}
	
	@ModuleParameter("script.name")
	public String testString;

	@ModuleParameter("script.byte1")
	public byte testByte1;

	@ModuleParameter("script.byte2")
	public Byte testByte2;

	@ModuleParameter("script.int1")
	public int testInteger1;

	@ModuleParameter("script.int2")
	public Integer testInteger2;

	@ModuleParameter("script.float1")
	public float testFloat1;
	
	@ModuleParameter("script.float2")
	public Float testFloat2;

	@ModuleParameter("script.double1")
	public double testDouble1;

	@ModuleParameter("script.double2")
	public Double testDouble2;
	
	@ModuleParameter("script.long1")
	public long testLong1;

	@ModuleParameter("script.long2")
	public Long testLong2;
	
	@ModuleParameter("script.numberAsString")
	public String numberAsString;
	
	@ModuleParameter("script.bool1")
	public boolean testBoolean1;

	@ModuleParameter("script.bool2")
	public Boolean testBoolean2;

	@ModuleParameter("script.booleanAsString")
	public String booleanAsString;
	
	@ModuleParameter("script.enum1")
	public TestEnum enum1;
	
	@ModuleParameter("script.enum2")
	public TestEnum enum2;
	
	@Override
	public boolean run(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity caster = spell.getCaster();
		if (caster == null) return false;

		if (!spellRing.taxCaster(spell, true)) return false;
		
		instance.runRunOverrides(spell, spellRing);
		
		spell.processEntity(caster, false);

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceShape instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		if (instance.runRenderOverrides(spell, spellRing)) return;

		Entity caster = spell.getCaster();
		World world = spell.world;

		if (caster == null) return;

		ParticleBuilder glitter = new ParticleBuilder(30);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		glitter.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
		glitter.enableMotionCalculation();
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(caster.getPositionVector()), 50, 5, (i, build) -> {
			double radius = 1;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			build.setPositionOffset(new Vec3d(
					RandUtil.nextDouble(-0.5, 0.5),
					RandUtil.nextDouble(-0.5, 0.5),
					RandUtil.nextDouble(-0.5, 0.5)
			));
			build.setScaleFunction(new InterpScale(RandUtil.nextFloat(0.2f, 1f), 0f));
			build.setMotion(new Vec3d(x, RandUtil.nextDouble(radius / 2.0, radius), z).normalize().scale(RandUtil.nextFloat()));
			build.setAcceleration(Vec3d.ZERO);
			build.setLifetime(50);
			build.setDeceleration(new Vec3d(0.8, 0.8, 0.8));

			if (RandUtil.nextBoolean()) {
				build.setColorFunction(new InterpColorHSV(spellRing.getPrimaryColor(), spellRing.getSecondaryColor()));
			} else {
				build.setColorFunction(new InterpColorHSV(spellRing.getSecondaryColor(), spellRing.getPrimaryColor()));
			}
		});

	}
}
