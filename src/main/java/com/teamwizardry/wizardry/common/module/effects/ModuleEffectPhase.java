package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.NullMovementInput;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

import static com.teamwizardry.wizardry.api.spell.SpellData.DefaultKeys.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectPhase extends Module {

	public ModuleEffectPhase() {
		super();
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Nonnull
	@Override
	public ItemStack getRequiredStack() {
		return new ItemStack(Items.ENDER_PEARL);
	}

	@Nonnull
	@Override
	public ModuleType getModuleType() {
		return ModuleType.EFFECT;
	}

	@Nonnull
	@Override
	public String getID() {
		return "phase";
	}

	@Nonnull
	@Override
	public String getReadableName() {
		return "Phase";
	}

	@Nonnull
	@Override
	public String getDescription() {
		return "Will short-range blink-trace you forwards";
	}

	@Override
	public double getManaDrain() {
		return 30;
	}

	@Override
	public double getBurnoutFill() {
		return 30;
	}

	@Override
	public int getChargeUpTime() {
		return 10;
	}

	@Override
	public boolean run(@Nonnull SpellData spell) {
		Entity caster = spell.getData(CASTER);
		Entity targetEntity = spell.getData(ENTITY_HIT);
		Vec3d targetHit = spell.getData(TARGET_HIT);

		int strength = 10;
		if (attributes.hasKey(Attributes.EXTEND))
			strength += Math.min(50, attributes.getDouble(Attributes.EXTEND));
		strength *= calcBurnoutPercent(caster);

		if (caster != null && targetEntity != null) {
			if (caster.getUniqueID().equals(targetEntity.getUniqueID())) {
				if (caster instanceof EntityLivingBase) {
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PHASE, strength, 1, true, false));
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PUSH, 1, 1, true, false));
				}
			} else {
				if (caster instanceof EntityLivingBase) {
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PHASE, strength, 1, true, false));
					((EntityLivingBase) caster).addPotionEffect(new PotionEffect(ModPotions.PUSH, 1, 1, true, false));
				}
			}
		}
		return true;
	}

	@Override
	public void runClient(@Nonnull SpellData spell) {

	}

	@Nullable
	@Override
	public Color getPrimaryColor() {
		return new Color(0x9595AA, true);
	}

	@Nonnull
	@Override
	public Module copy() {
		return cloneModule(new ModuleEffectPhase());
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null && mc.player.isPotionActive(ModPotions.PHASE)) {
			EntityPlayerSP player = mc.player;
			if (!(player.movementInput instanceof NullMovementInput))
				player.movementInput = new NullMovementInput(player.movementInput);
		}
	}
}
