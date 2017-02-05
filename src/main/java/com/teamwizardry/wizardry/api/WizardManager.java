package com.teamwizardry.wizardry.api;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;

/**
 * Created by LordSaad.
 */
public class WizardManager {

	public static WizardManager INSTANCE = new WizardManager();

	private WizardManager() {
	}

	public static void setMaxMana(int mana, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setMaxMana(mana, (EntityPlayer) player);
		if (getMana(player) > mana) setMana(mana, player);
	}

	public static void setMana(int mana, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setMana(Math.min(Math.max(0, mana), getMaxMana(player)), (EntityPlayer) player);
	}

	public static void addMana(int mana, @Nullable EntityLivingBase player) {
		setMana(getMana(player) + mana, player);
	}

	public static void removeMana(int mana, @Nullable EntityLivingBase player) {
		setMana(getMana(player) - mana, player);
	}

	public static void setMaxBurnout(int burnout, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setMaxBurnout(burnout, (EntityPlayer) player);
		if (getBurnout(player) > burnout) setBurnout(burnout, player);
	}

	public static void setBurnout(int burnout, @Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return;
		cap.setBurnout(Math.max(0, Math.min(burnout, getMaxBurnout(player))), (EntityPlayer) player);
	}

	public static void addBurnout(int burnout, @Nullable EntityLivingBase player) {
		setBurnout(getBurnout(player) + burnout, player);
	}

	public static void removeBurnout(int burnout, @Nullable EntityLivingBase player) {
		setBurnout(getBurnout(player) - burnout, player);
	}

	public static int getMaxMana(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getMaxMana();
	}

	public static int getMana(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getMana();
	}

	public static int getBurnout(@Nullable EntityLivingBase player) {
		IWizardryCapability cap = getCap(player);
		if (cap == null) return 0;
		return cap.getBurnout();
	}

	public static int getMaxBurnout(@Nullable EntityLivingBase player) {
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
