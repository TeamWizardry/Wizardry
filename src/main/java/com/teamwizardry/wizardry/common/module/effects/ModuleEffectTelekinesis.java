package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.InterpScale;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.CASTER;
import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.TARGET_HIT;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectTelekinesis extends Module implements IContinousSpell {

	public ModuleEffectTelekinesis() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_telekinesis";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Telekinesis";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will levitate blocks and entities in the world";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d targetPos = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);

		double strength = 3;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(8.0, attributes.getDouble(Attributes.EXTEND));
		if (!processCost(strength, spell)) return false;
		strength *= calcBurnoutPercent(caster);

		if (targetPos == null) return false;

		List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(new BlockPos(targetPos)).expand(strength, strength, strength));

		for (Entity entity : entityList) {
			double dist = entity.getPositionVector().distanceTo(targetPos);
			if (dist > strength) continue;

			final double upperMag = 1;
			final double scale = 1;
			double mag = upperMag * (scale * dist / (-scale * dist - 1) + 1);

			Vec3d dir = targetPos.subtract(entity.getPositionVector()).normalize().scale(mag);

			entity.motionX = (dir.xCoord);
			entity.motionY = (dir.yCoord);
			entity.motionZ = (dir.zCoord);
			entity.fallDistance = 0;
			entity.velocityChanged = true;

		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d position = spell.getData(TARGET_HIT);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(50);
		glitter.setColorFunction(new InterpColorHSV(getPrimaryColor(), getSecondaryColor()));
		glitter.setScale(1);
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));

		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), 5, 0, (aFloat, particleBuilder) -> {
			glitter.setLifetime(ThreadLocalRandom.current().nextInt(10, 20));
			glitter.setScale(ThreadLocalRandom.current().nextFloat());
			glitter.setScaleFunction(new InterpScale(1, 0));
			glitter.setAlphaFunction(new InterpFadeInOut(0.3f, ThreadLocalRandom.current().nextFloat()));
			glitter.setMotion(new Vec3d(
					ThreadLocalRandom.current().nextDouble(-0.1, 0.1),
					ThreadLocalRandom.current().nextDouble(-0.1, 0.1),
					ThreadLocalRandom.current().nextDouble(-0.1, 0.1)
			));
		});
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectTelekinesis());
	}


	@SubscribeEvent
	public void tickEntity(TickEvent.WorldTickEvent event) {
		//if (event.)
	}
}
