package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.ContextRing;
import com.teamwizardry.wizardry.api.spell.annotation.ModuleOverride;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID = "effect_frost")
public class ModuleEffectFrost implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe", "modifier_extend_time"};
	}

	@ModuleOverride("shape_zone_run")
	public boolean onRunZone(World world, SpellData data, SpellRing ring, @ContextRing SpellRing childRing) {
		if(!world.isRemote) return false;

		double aoe = ring.getAttributeValue(world, AttributeRegistry.AREA, data);
		double range = ring.getAttributeValue(world, AttributeRegistry.RANGE, data);

		Vec3d targetPos = data.getTarget(world);

		if (targetPos == null) return false;

		Vec3d min = targetPos.subtract(aoe, range, aoe);
		Vec3d max = targetPos.add(aoe, range, aoe);

		List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(min, max));
		for (Entity entity : entities) {
			entity.extinguish();
			if (entity instanceof EntityLivingBase) {
				if (!((EntityLivingBase) entity).isPotionActive(ModPotions.SLIPPERY) && entity.getDistanceSq(targetPos.x, targetPos.y, targetPos.z) <= aoe * aoe) {

					double time = childRing.getAttributeValue(world, AttributeRegistry.DURATION, data) * 10;
					world.playSound(null, entity.getPosition(), ModSounds.FROST_FORM, CommonProxy.SC_Wizardry, 1, 1);
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(ModPotions.SLIPPERY, (int) time, 0, true, false));
				}
			}
		}
		return false;
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster(world);

		double range = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell) / 2;
		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(world, spell, true)) return false;

		if (targetEntity != null) {
			world.playSound(null, targetEntity.getPosition(), ModSounds.FROST_FORM, CommonProxy.SC_Wizardry, 1, 1);
			targetEntity.extinguish();
			if (targetEntity instanceof EntityLivingBase) {
				((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.SLIPPERY, (int) time, 0, true, false));
			}
		}

		if (targetPos != null) {
			world.playSound(null, targetPos, ModSounds.FROST_FORM, CommonProxy.SC_Wizardry, 1, 1);
			for (BlockPos pos : BlockPos.getAllInBox(targetPos.add(-range, -range, -range), targetPos.add(range + 1, range + 1, range + 1))) {
				double dist = pos.distanceSq(targetPos);
				if (dist > range) continue;

				for (EnumFacing facing : EnumFacing.VALUES) {
					IBlockState state = world.getBlockState(pos.offset(facing));
					if (state.getBlock() == Blocks.FIRE) {
						BlockUtils.breakBlock(world, pos.offset(facing), state, BlockUtils.makeBreaker(world, pos, caster));
					}
				}

				BlockPos up = pos.offset(EnumFacing.UP);
				if (world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP) && world.isAirBlock(up)) {
					int layerSize = (int) (Math.max(1, Math.min(8, Math.max(1, (dist / range) * 6.0))));
					layerSize = Math.max(1, Math.min(layerSize + RandUtil.nextInt(-1, 1), 8));
					BlockUtils.placeBlock(world, up, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, layerSize), BlockUtils.makePlacer(world, up, caster));
				}

				if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
					BlockUtils.placeBlock(world, pos, Blocks.ICE.getDefaultState(), BlockUtils.makePlacer(world, pos, caster));
				}
			}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setAlphaFunction(new InterpFloatInOut(0.0f, 0.1f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
		glitter.enableMotionCalculation();
		glitter.setScaleFunction(new InterpScale(1, 0));
		glitter.setAcceleration(new Vec3d(0, -0.02, 0));
		glitter.setCollision(true);
		glitter.setCanBounce(true);
		ParticleSpawner.spawn(glitter, world, new StaticInterp<>(position), RandUtil.nextInt(5, 15), 0, (aFloat, particleBuilder) -> {
			double radius = 2;
			double theta = 2.0f * (float) Math.PI * RandUtil.nextFloat();
			double r = radius * RandUtil.nextFloat();
			double x = r * MathHelper.cos((float) theta);
			double z = r * MathHelper.sin((float) theta);
			glitter.setScale(RandUtil.nextFloat());
			glitter.setPositionOffset(new Vec3d(x, RandUtil.nextDouble(-2, 2), z));
			glitter.setLifetime(RandUtil.nextInt(50, 100));
			Vec3d direction = position.add(glitter.getPositionOffset()).subtract(position).normalize().scale(1 / 5);
			glitter.addMotion(direction.scale(RandUtil.nextDouble(0.5, 1)));
		});

	}
}
