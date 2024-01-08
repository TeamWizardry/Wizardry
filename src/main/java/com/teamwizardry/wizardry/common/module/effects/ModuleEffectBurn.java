package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModSounds;
import com.teamwizardry.wizardry.proxy.CommonProxy;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.Color;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.FACE_HIT;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_burn")
public class ModuleEffectBurn implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_increase_aoe", "modifier_extend_time"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {

		Entity targetEntity = spell.getVictim(world);
		BlockPos targetPos = spell.getTargetPos();
		Entity caster = spell.getCaster(world);
		EnumFacing facing = spell.getData(FACE_HIT);

		double area = spellRing.getAttributeValue(world, AttributeRegistry.AREA, spell) / 2.0;
		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell);

		if (!spellRing.taxCaster(world, spell, true)) return false;

		if (targetEntity != null) {
			targetEntity.setFire((int) time);
			world.playSound(null, targetEntity.getPosition(), ModSounds.FIRE, CommonProxy.SC_Wizardry, RandUtil.nextFloat(0.35f, 0.75f), RandUtil.nextFloat(0.35f, 1.5f));
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
							BlockUtils.placeBlock(world, pos.offset(facing), Blocks.FIRE.getDefaultState(), BlockUtils.makePlacer(world, pos, caster));
						} else for (EnumFacing face : EnumFacing.VALUES) {
							if (world.isAirBlock(pos.offset(face)) || world.getBlockState(pos.offset(face)).getBlock() == Blocks.SNOW_LAYER) {
								BlockUtils.placeBlock(world, pos.offset(face), Blocks.AIR.getDefaultState(), BlockUtils.makePlacer(world, pos, caster));
							}
						}
					}
			world.playSound(null, targetPos, ModSounds.FIRE, SoundCategory.AMBIENT, 0.5f, RandUtil.nextFloat());
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		Color color = instance.getPrimaryColor();
		if (RandUtil.nextBoolean()) color = instance.getSecondaryColor();

		LibParticles.EFFECT_BURN(world, position, color);
	}
}
