package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

/**
 * Created by LordSaad.
 */
public class WizardManager {

	public static WizardManager INSTANCE = new WizardManager();

	private WizardManager() {
	}

	public static void setMaxMana(double mana, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setMaxMana(mana, (EntityPlayer) player);
		if (getMana(player) > mana) setMana(mana, player);
	}

	public static void setMana(double mana, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setMana(Math.min(Math.max(0, mana), getMaxMana(player)), (EntityPlayer) player);
	}

	public static void addMana(double mana, @Nullable EntityLivingBase player) {
		setMana(getMana(player) + mana, player);
	}

	public static void removeMana(double mana, @Nullable EntityLivingBase player) {
		setMana(getMana(player) - mana, player);
	}

	public static void setMaxBurnout(double burnout, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setMaxBurnout(burnout, (EntityPlayer) player);
		if (getBurnout(player) > burnout) setBurnout(burnout, player);
	}

	public static void setBurnout(double burnout, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setBurnout(Math.max(0, Math.min(burnout, getMaxBurnout(player))), (EntityPlayer) player);
	}

	public static void addBurnout(double burnout, @Nullable EntityLivingBase player) {
		setBurnout(getBurnout(player) + burnout, player);
	}

	public static void removeBurnout(double burnout, @Nullable EntityLivingBase player) {
		setBurnout(getBurnout(player) - burnout, player);
	}

	public static double getMaxMana(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getMaxMana();
	}

	public static double getMana(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getMana();
	}

	public static double getBurnout(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getBurnout();
	}

	public static double getMaxBurnout(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getMaxBurnout();
	}

	@Nullable
	public static IWizardryCapability getCap(EntityLivingBase player) {
		if (player != null && player instanceof EntityPlayer)
			return WizardryCapabilityProvider.get((EntityPlayer) player);
		return null;
	}
}
