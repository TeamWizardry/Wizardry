package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpFadeInOut;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.ModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule
public class ModuleEffectFrost extends ModuleEffect {

	@Nonnull
	@Override
	public String getID() {
		return "effect_frost";
	}

	@Override
	public ModuleModifier[] applicableModifiers() {
		return new ModuleModifier[]{new ModuleModifierIncreaseAOE(), new ModuleModifierIncreaseDuration()};
	}

	@Override
	public boolean run(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity targetEntity = spell.getVictim();
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster();

		double range = spellRing.getAttributeValue(AttributeRegistry.AREA, spell) / 2;
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(spell)) return false;

		if (targetEntity != null) {
			spell.world.playSound(null, targetEntity.getPosition(), ModSounds.FROST_FORM, SoundCategory.NEUTRAL, 1, 1);
			targetEntity.extinguish();
			if (targetEntity instanceof EntityLivingBase)
				((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.SLIPPERY, (int) time, 0, true, false));
		}

		if (targetPos != null) {
			spell.world.playSound(null, targetPos, ModSounds.FROST_FORM, SoundCategory.NEUTRAL, 1, 1);
			for (int x = (int) range; x >= -range; x--)
				for (int y = (int) range; y >= -range; y--)
					for (int z = (int) range; z >= -range; z--) {
						BlockPos pos = targetPos.add(x, y, z);
						double dist = pos.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ());
						if (dist > range) continue;

						for (EnumFacing facing : EnumFacing.VALUES) {
							IBlockState state = world.getBlockState(pos.offset(facing));
							if (state.getBlock() == Blocks.FIRE) {
								BlockUtils.breakBlock(world, pos.offset(facing), state, caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null, false);
							}
						}

						if (world.getBlockState(pos).isSideSolid(world, pos, EnumFacing.UP) && world.isAirBlock(pos.offset(EnumFacing.UP))) {
							int layerSize = (int) (Math.max(1, Math.min(8, Math.max(1, (dist / range) * 6.0))));
							layerSize = Math.max(1, Math.min(layerSize + RandUtil.nextInt(-1, 1), 8));
							BlockUtils.placeBlock(world, pos.offset(EnumFacing.UP), Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, layerSize), caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null);
						}

						if (world.getBlockState(pos).getBlock() == Blocks.WATER) {
							BlockUtils.placeBlock(world, pos, Blocks.ICE.getDefaultState(), caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null);
						}
					}
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(@Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		ParticleBuilder glitter = new ParticleBuilder(1);
		glitter.setAlphaFunction(new InterpFadeInOut(0.0f, 0.1f));
		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
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
