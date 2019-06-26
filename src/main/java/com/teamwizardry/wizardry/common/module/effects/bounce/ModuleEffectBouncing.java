package com.teamwizardry.wizardry.common.module.effects.bounce;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.librarianlib.features.math.interpolate.StaticInterp;
import com.teamwizardry.librarianlib.features.math.interpolate.numeric.InterpFloatInOut;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.particle.ParticleBuilder;
import com.teamwizardry.librarianlib.features.particle.ParticleSpawner;
import com.teamwizardry.librarianlib.features.particle.functions.InterpColorHSV;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.spell.ILingeringModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.annotation.RegisterModule;
import com.teamwizardry.wizardry.api.spell.attribute.AttributeRegistry;
import com.teamwizardry.wizardry.api.spell.module.IModuleEffect;
import com.teamwizardry.wizardry.api.spell.module.ModuleInstanceEffect;
import com.teamwizardry.wizardry.api.util.BlockUtils;
import com.teamwizardry.wizardry.api.util.RandUtil;
import com.teamwizardry.wizardry.api.util.interp.InterpScale;
import com.teamwizardry.wizardry.init.ModPotions;
import com.teamwizardry.wizardry.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@RegisterModule(ID = "effect_bouncing")
public class ModuleEffectBouncing implements IModuleEffect, ILingeringModule {

	@Override
	public String[] compatibleModifiers() {
		return new String[]{"modifier_extend_time"};
	}

	@Override
	public boolean runOnStart(@Nonnull World world, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		Entity entity = spell.getVictim(world);

		BlockPos pos = spell.getTargetPos();
		if (pos == null) return true;

		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;

		if (!spellRing.taxCaster(world, spell, true)) return false;

		if (entity instanceof EntityLivingBase) {
			BounceManager.INSTANCE.forEntity((EntityLivingBase) entity, (int) time);
		} else if (!BlockUtils.isAnyAir(world, pos)) {
			BounceManager.INSTANCE.forBlock(world, pos, (int) time);
			PacketHandler.NETWORK.sendToAll(new PacketAddBouncyBlock(world, pos, (int) time));
		}

		world.playSound(null, pos, ModSounds.SLIME_SQUISHING, SoundCategory.NEUTRAL, RandUtil.nextFloat(0.6f, 1f), RandUtil.nextFloat(0.5f, 1f));
		return true;
	}

	@Override
	public boolean run(@NotNull World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderSpell(World world, ModuleInstanceEffect instance, @Nonnull SpellData spell, @Nonnull SpellRing spellRing) {

		Entity target = spell.getVictim(world);

		if (ClientTickHandler.getTicks() % 10 == 0) return;
		if (target instanceof EntityLivingBase && ((EntityLivingBase) target).isPotionActive(ModPotions.BOUNCING)) {

			ParticleBuilder glitter = new ParticleBuilder(30);
			glitter.setColorFunction(new InterpColorHSV(instance.getPrimaryColor(), instance.getSecondaryColor()));
			glitter.setRender(new ResourceLocation(Wizardry.MODID, NBTConstants.MISC.SPARKLE_BLURRED));
			glitter.enableMotionCalculation();
			glitter.setCollision(true);

			ParticleSpawner.spawn(glitter, world, new StaticInterp<>(target.getPositionVector().add(RandUtil.nextDouble(-0.5, 0.5), RandUtil.nextDouble(0, target.height / 3.0), RandUtil.nextDouble(-0.5, 0.5))), 1, 0, (aFloat, particleBuilder) -> {
				particleBuilder.setLifetime(RandUtil.nextInt(20, 25));
				particleBuilder.setScaleFunction(new InterpScale(RandUtil.nextFloat(1f, 1.5f), 0f));
				particleBuilder.setAlphaFunction(new InterpFloatInOut(0.3f, 0.3f));
				particleBuilder.setAcceleration(new Vec3d(0, RandUtil.nextDouble(-0.005, -0.01), 0));
			});
		}
	}

	@Override
	public int getLingeringTime(World world, SpellData spell, SpellRing spellRing) {
		double time = spellRing.getAttributeValue(world, AttributeRegistry.DURATION, spell) * 10;
		return (int) time;
	}
}
