package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.LightningGenerator;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.network.PacketRenderLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectLightning extends Module implements ITaxing {

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "effect_lightning";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Lightning";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will shock a target, stunning it.";
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		World world = spell.world;
		Vec3d target = spell.getData(TARGET_HIT);
		Entity caster = spell.getData(CASTER);
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);

		if (target == null) return false;

		Vec3d origin = target;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, caster.getEyeHeight(), offZ).add(target);
		}

		double range = getModifierPower(spell, Attributes.EXTEND_RANGE, 10, 32, true, true);
		double strength = getModifierPower(spell, Attributes.INCREASE_POTENCY, 1, 10, true, true);

		if (!tax(this, spell)) return false;

		RayTraceResult traceResult = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), target, range, caster);
		if (traceResult == null) return false;

		long seed = RandUtil.nextLong(100, 100000);

		spell.addData(SEED, seed);

		LightningGenerator generator = new LightningGenerator(origin, traceResult.hitVec, new RandUtilSeed(seed));

		ArrayList<Vec3d> points = generator.generate();

		for (Vec3d point : points) {
			List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity(caster, new AxisAlignedBB(new BlockPos(point)).contract(0.2, 0.2, 0.2));
			if (!entityList.isEmpty()) {
				for (Entity entity : entityList) {
					entity.setFire((int) (strength));
					if (caster instanceof EntityPlayer)
						entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) caster), (float) (strength));
					else entity.attackEntityFrom(DamageSource.LIGHTNING_BOLT, (float) (strength));
				}
			}
		}

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Entity caster = spell.getData(CASTER);
		Vec3d target = spell.getData(TARGET_HIT);
		long seed = spell.getData(SEED, 0L);

		if (target == null) return;

		Vec3d origin = target;
		if (caster != null) {
			float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - yaw));
			float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - yaw));
			origin = new Vec3d(offX, 0, offZ).add(target);
		}

		RayTraceResult traceResult = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), origin, 10, caster);
		if (traceResult == null) return;

		PacketHandler.NETWORK.sendToAllAround(new PacketRenderLightningBolt(origin, traceResult.hitVec, seed),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), origin.xCoord, origin.yCoord, origin.zCoord, 256));
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectLightning());
	}
}
