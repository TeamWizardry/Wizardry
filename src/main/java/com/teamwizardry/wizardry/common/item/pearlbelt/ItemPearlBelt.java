package com.teamwizardry.wizardry.common.item.pearlbelt;

import baubles.api.BaubleType;
import com.teamwizardry.librarianlib.features.base.item.ItemModBauble;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.capability.item.ProxiedItemStackHandler;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles")
public class ItemPearlBelt extends ItemModBauble implements IPearlBelt {

	public ItemPearlBelt() {
		super("pearl_belt");
		setMaxStackSize(1);

		addBeltColorProperty(this);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull World world, @NotNull EntityPlayer player, @NotNull EnumHand hand) {

		player.openGui(Wizardry.instance, 1, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Nonnull
	@Override
	@Optional.Method(modid = "baubles")
	public BaubleType getBaubleType(@NotNull ItemStack stack) {
		return BaubleType.BELT;
	}

	@Nullable
	@Override
	public IItemHandler getPearls(ItemStack stack) {
		return getBeltPearls(stack);
	}

	@Nullable
	@Override
	@SideOnly(Side.CLIENT)
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return getBeltColorFunction();
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new BeltItemHandler(stack);
	}

	public static class BeltItemHandler extends ProxiedItemStackHandler {
		public BeltItemHandler(ItemStack stack) {
			super(stack, ConfigValues.pearlBeltInvSize);
		}

		@Override
		protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
			return ConfigValues.pearlBeltInvSize;
		}
	}

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
