package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.util.PosUtils;
import com.teamwizardry.wizardry.api.util.Utils;
import com.teamwizardry.wizardry.common.network.PacketLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectLightning extends Module {

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
		BlockPos targetPos = spell.getData(BLOCK_HIT);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Entity caster = spell.getData(CASTER);

		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {
		World world = spell.world;
		float yaw = spell.getData(YAW, 0F);
		float pitch = spell.getData(PITCH, 0F);
		Entity entity = spell.getData(CASTER);
		Vec3d direction = PosUtils.vecFromRotations(pitch, yaw);

		if (entity == null) return;
		Vec3d position = entity.getPositionVector();//spell.getData(TARGET_HIT);

		RayTraceResult traceResult = Utils.raytrace(world, PosUtils.vecFromRotations(pitch, yaw), position, 10, entity);
		if (traceResult == null) return;

		PacketHandler.NETWORK.sendToAllAround(new PacketLightningBolt(entity.getPositionVector(), traceResult.hitVec),
				new NetworkRegistry.TargetPoint(world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, 256));
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectLightning());
	}
}
