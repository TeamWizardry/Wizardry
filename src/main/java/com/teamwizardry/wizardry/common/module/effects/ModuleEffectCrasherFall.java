package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.client.fx.LibParticles;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque.
 */
@RegisterModule(ID="effect_crasher_fall")
public class ModuleEffectCrasherFall implements IModuleEffect {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_range", "modifier_extend_time"};
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity targetEntity = spell.getVictim(world);

		if (targetEntity instanceof EntityLivingBase) {
			double strength = spellRing.getAttributeValue(world, AttributeRegistry.RANGE, spell);
			double duration = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
			if (!spellRing.taxCaster(world, spell, true)) return false;

			((EntityLivingBase) targetEntity).addPotionEffect(new PotionEffect(ModPotions.CRASH, (int) duration, (int) strength, true, true));
		}
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Vec3d position = spell.getTarget(world);

		if (position == null) return;

		LibParticles.EFFECT_REGENERATE(world, position, instance.getPrimaryColor());
	}
}
