package com.teamwizardry.wizardry.api.capability.player.mana;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.common.network.PacketUpdateManaCap;
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
public class BaubleManaCapability implements IManaCapability {

	private ItemStack stack;

	public BaubleManaCapability(ItemStack stack) {
		this.stack = stack;
	}

	public BaubleManaCapability(ItemStack stack, double maxMana, double maxBurnout) {
		this.stack = stack;
		NBTHelper.setDouble(stack, "mana", 0);
		NBTHelper.setDouble(stack, "max_mana", maxMana);
		NBTHelper.setDouble(stack, "burnout", 0);
		NBTHelper.setDouble(stack, "max_burnout", maxBurnout);
	}

	public BaubleManaCapability(ItemStack stack, double maxMana, double maxBurnout, double mana, double burnout) {
		this.stack = stack;
		NBTHelper.setDouble(stack, "mana", mana);
		NBTHelper.setDouble(stack, "max_mana", maxMana);
		NBTHelper.setDouble(stack, "burnout", burnout);
		NBTHelper.setDouble(stack, "max_burnout", maxBurnout);
	}

	@Override
	public double getMana() {
		return NBTHelper.getDouble(stack, "mana", 0);
	}

	@Override
	public void setMana(double mana) {
		NBTHelper.setDouble(stack, "mana", MathHelper.clamp(mana, 0, NBTHelper.getDouble(stack, "max_mana", 0)));
	}

	@Override
	public double getMaxMana() {
		return NBTHelper.getDouble(stack, "max_mana", 0);
	}

	@Override
	public void setMaxMana(double maxMana) {
		NBTHelper.setDouble(stack, "max_mana", 0);
	}

	@Override
	public double getBurnout() {
		return NBTHelper.getDouble(stack, "burnout", 0);
	}

	@Override
	public void setBurnout(double burnout) {
		NBTHelper.setDouble(stack, "burnout", MathHelper.clamp(burnout, 0, NBTHelper.getDouble(stack, "max_burnout", 0)));
	}

	@Override
	public double getMaxBurnout() {
		return NBTHelper.getDouble(stack, "max_burnout", 0);
	}

	@Override
	public void setMaxBurnout(double maxBurnout) {
		NBTHelper.setDouble(stack, "max_burnout", 0);
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
			PacketHandler.NETWORK.sendTo(new PacketUpdateManaCap(serializeNBT()), (EntityPlayerMP) entity);
	}
}
