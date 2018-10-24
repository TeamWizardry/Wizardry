package com.teamwizardry.wizardry.common.module.effects;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.IModuleModifier;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.spell.module.RegisterModule;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseAOE;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierIncreaseDuration;
import com.teamwizardry.wizardry.init.ModSounds;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_burn")
public class ModuleEffectBurn implements IModuleEffect {

	@Override
	public String[] compatibleModifierClasses() {
		return new String[]{"modifier_increase_aoe", "modifier_extend_time"};
	}

	@Override
	public boolean run(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Entity targetEntity = spell.getVictim();
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster();
		EnumFacing facing = spell.getData(FACE_HIT);

		double area = spellRing.getAttributeValue(AttributeRegistry.AREA, spell) / 2.0;
		double time = spellRing.getAttributeValue(AttributeRegistry.DURATION, spell);

		if (!spellRing.taxCaster(spell, true)) return false;

		if (targetEntity != null) {
			targetEntity.setFire((int) time);
			world.playSound(null, targetEntity.getPosition(), ModSounds.FIRE, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.35f, 0.75f), RandUtil.nextFloat(0.35f, 1.5f));
		}

		if (targetPos != null) {
			for (int x = (int) area; x >= -area; x--)
				for (int y = (int) area; y >= -area; y--)
					for (int z = (int) area; z >= -area; z--) {
						BlockPos pos = targetPos.add(x, y, z);
						double dist = pos.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ());
						if (dist > area) continue;
						if (facing != null) {
							if (!world.isAirBlock(pos.offset(facing))) continue;
							BlockUtils.placeBlock(world, pos.offset(facing), Blocks.FIRE.getDefaultState(), caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null);
						} else for (EnumFacing face : EnumFacing.VALUES) {
							if (world.isAirBlock(pos.offset(face)) || world.getBlockState(pos.offset(face)).getBlock() == Blocks.SNOW_LAYER) {
								BlockUtils.placeBlock(world, pos.offset(face), Blocks.AIR.getDefaultState(), caster instanceof EntityPlayer ? (EntityPlayerMP) caster : null);
							}
						}
					}
			world.playSound(null, targetPos, ModSounds.FIRE, SoundCategory.AMBIENT, 0.5f, RandUtil.nextFloat());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		World world = spell.world;
		Vec3d position = spell.getTarget();

		if (position == null) return;

		Color color = instance.getPrimaryColor();
		if (RandUtil.nextBoolean()) color = instance.getSecondaryColor();

		LibParticles.EFFECT_BURN(world, position, color);
	}
}
