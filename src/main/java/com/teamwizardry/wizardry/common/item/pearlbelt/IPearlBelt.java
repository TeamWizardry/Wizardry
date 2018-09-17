package com.teamwizardry.wizardry.common.item.pearlbelt;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.item.wheels.IPearlWheelHolder;
import com.teamwizardry.wizardry.common.network.PacketAddPearlsToBelt;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModSounds;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public interface IPearlBelt extends IPearlWheelHolder, INacreProduct.INacreDecayProduct {

	default TriConsumer<EntityPlayer, ItemStack, Integer> beltRadialUISelection() {
		return (player, holder, selection) -> {
			int scrollSlot = ItemNBTHelper.getInt(holder, "scroll_slot", -1);
			if (scrollSlot < 0) return;

			ItemStack stack = removePearl(holder, scrollSlot);
			if (stack.isEmpty()) return;

			player.addItemStackToInventory(stack);
		};
	}

	default void addBeltColorProperty(Item item) {
		item.addPropertyOverride(new ResourceLocation("slot"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				ItemStackHandler handler = getPearls(stack);
				if (handler == null) return 0;

				int total = 0;
				for (int i = 0; i < handler.getSlots(); i++) {
					ItemStack pearl = handler.getStackInSlot(i);
					if (pearl.isEmpty()) continue;

					total++;
				}

				return MathHelper.clamp(total, 0, 6);
			}
		});
	}

	default void onRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) return;

		if (!shouldUse(player.getHeldItem(hand))) return;

		boolean changed = false;
		for (ItemStack stack : player.inventory.mainInventory)
			if (stack.getItem() == ModItems.PEARL_NACRE)
				if (ItemNBTHelper.getBoolean(stack, "infused", false))
					if (addPearl(player.getHeldItem(hand), stack.copy())) {
						stack.shrink(1);
						changed = true;
					}

		if (changed) {
			if (player instanceof EntityPlayerMP)
				PacketHandler.NETWORK.sendTo(new PacketAddPearlsToBelt(player.inventory.getSlotFor(player.getHeldItem(hand))), (EntityPlayerMP) player);
			player.playSound(ModSounds.BELL_TING, 1f, 1f);
		}
	}

	default void initBelt(ItemStack stack, @Nullable NBTTagCompound nbt) {
		if (nbt == null) nbt = new NBTTagCompound();
		nbt.setTag("inv", new ItemStackHandler(ConfigValues.pearlBeltInvSize).serializeNBT());
		stack.setTagCompound(nbt);
	}

	default ItemStackHandler getBeltPearls(ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		ItemStackHandler handler = null;
		if (tag == null || !tag.hasKey("inv")) {
			tag = new NBTTagCompound();
			handler = new ItemStackHandler(ConfigValues.pearlBeltInvSize);

			tag.setTag("inv", new ItemStackHandler(ConfigValues.pearlBeltInvSize).serializeNBT());
			stack.setTagCompound(handler.serializeNBT());
		}

		if (tag.hasKey("inv")) {
			NBTTagCompound inv = tag.getCompoundTag("inv");
			handler = new ItemStackHandler();
			handler.deserializeNBT(inv);
		}

		return handler;
	}

	default Function2<ItemStack, Integer, Integer> getBeltColorFunction() {
		return (pearlBelt, tintIndex) -> {
			if (tintIndex == 0) return 0xFFFFFF;

			IItemHandler handler = getPearls(pearlBelt);
			if (handler == null) return 0;

			ItemStack stack = handler.getStackInSlot(tintIndex - 1);
			if (stack.isEmpty()) return 0xFFFFFF;

			if (!stack.hasTagCompound())
				return Color.HSBtoRGB(MathHelper.sin(Minecraft.getMinecraft().world.getTotalWorldTime() / 140f), 0.75f, 1f);

			long lastCast = ItemNBTHelper.getLong(stack, Constants.NBT.LAST_CAST, -1);
			int decayCooldown = ItemNBTHelper.getInt(stack, Constants.NBT.LAST_COOLDOWN, -1);
			long tick = Minecraft.getMinecraft().world.getTotalWorldTime();
			long timeSinceCooldown = tick - lastCast;
			float decayStage = (decayCooldown > 0) ? ((float) timeSinceCooldown) / decayCooldown : 1f;


			float rand = ItemNBTHelper.getFloat(stack, Constants.NBT.RAND, -1);
			float hue = rand < 0 ? (tick / 140f) % 140f : rand;
			float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

			double decaySaturation = (lastCast == -1 || decayCooldown <= 0 || decayStage >= 1f) ? 1f :
					(decayStage < decayCurveDelimiter) ? Math.pow(Math.E, -15 * decayStage) : Math.pow(Math.E, 3 * decayStage - 3);

			float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow)) * (float) decaySaturation;

			return Color.HSBtoRGB(hue, saturation, 1f);
		};
	}
}
