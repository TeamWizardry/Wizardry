package com.teamwizardry.wizardry.api.capability;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.common.network.PacketUpdateCaps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque.
 */
public class BaubleWizardryCapability implements IWizardryCapability {

	private ItemStack stack;

	public BaubleWizardryCapability(ItemStack stack) {
		this.stack = stack;
	}

	public BaubleWizardryCapability(ItemStack stack, double maxMana, double maxBurnout) {
		this.stack = stack;
		ItemNBTHelper.setDouble(stack, "mana", 0);
		ItemNBTHelper.setDouble(stack, "max_mana", maxMana);
		ItemNBTHelper.setDouble(stack, "burnout", 0);
		ItemNBTHelper.setDouble(stack, "max_burnout", maxBurnout);
	}

	public BaubleWizardryCapability(ItemStack stack, double maxMana, double maxBurnout, double mana, double burnout) {
		this.stack = stack;
		ItemNBTHelper.setDouble(stack, "mana", mana);
		ItemNBTHelper.setDouble(stack, "max_mana", maxMana);
		ItemNBTHelper.setDouble(stack, "burnout", burnout);
		ItemNBTHelper.setDouble(stack, "max_burnout", maxBurnout);
	}

	@Override
	public double getMana() {
		return ItemNBTHelper.getDouble(stack, "mana", 0);
	}

	@Override
	public void setMana(double mana) {
		ItemNBTHelper.setDouble(stack, "mana", MathHelper.clamp(mana, 0, ItemNBTHelper.getDouble(stack, "max_mana", 0)));
	}

	@Override
	public double getMaxMana() {
		return ItemNBTHelper.getDouble(stack, "max_mana", 0);
	}

	@Override
	public void setMaxMana(double maxMana) {
		ItemNBTHelper.setDouble(stack, "max_mana", 0);
	}

	@Override
	public double getBurnout() {
		return ItemNBTHelper.getDouble(stack, "burnout", 0);
	}

	@Override
	public void setBurnout(double burnout) {
		ItemNBTHelper.setDouble(stack, "burnout", MathHelper.clamp(burnout, 0, ItemNBTHelper.getDouble(stack, "max_burnout", 0)));
	}

	@Override
	public double getMaxBurnout() {
		return ItemNBTHelper.getDouble(stack, "max_burnout", 0);
	}

	@Override
	public void setMaxBurnout(double maxBurnout) {
		ItemNBTHelper.setDouble(stack, "max_burnout", 0);
	}

	@Override
	@Nullable
	public EnumBloodType getBloodType() {
		return null;
	}

	@Override
	public void setBloodType(EnumBloodType bloodType) {
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return stack.getTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound compound) {
		stack.setTagCompound(compound);
	}

	@Override
	public void dataChanged(Entity entity) {
		if ((entity != null) && entity instanceof EntityPlayer && !entity.getEntityWorld().isRemote)
			PacketHandler.NETWORK.sendTo(new PacketUpdateCaps(serializeNBT()), (EntityPlayerMP) entity);
	}
}
