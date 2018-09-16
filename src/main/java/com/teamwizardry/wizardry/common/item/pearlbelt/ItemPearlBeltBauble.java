package com.teamwizardry.wizardry.common.item.pearlbelt;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemPearlBeltBauble extends ItemModBauble implements IPearlBelt {

	public ItemPearlBeltBauble() {
		super("pearl_belt");
		setMaxStackSize(1);

		addBeltColorProperty(this);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull World world, @NotNull EntityPlayer player, @NotNull EnumHand hand) {
		onRightClick(world, player, hand);

		return super.onItemRightClick(world, player, hand);
	}

	@Nonnull
	@Optional.Method(modid = "baubles")
	@Override
	public BaubleType getBaubleType(@NotNull ItemStack stack) {
		return BaubleType.BELT;
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		initBelt(stack, nbt);
		return null;
	}

	@Nullable
	@Override
	public ItemStackHandler getPearls(ItemStack stack) {
		return getBeltPearls(stack);
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return getBeltColorFunction();
	}
}
