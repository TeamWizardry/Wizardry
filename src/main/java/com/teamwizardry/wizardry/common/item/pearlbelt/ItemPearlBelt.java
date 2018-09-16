package com.teamwizardry.wizardry.common.item.pearlbelt;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ItemPearlBelt extends ItemMod implements IPearlBelt {

	public ItemPearlBelt() {
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