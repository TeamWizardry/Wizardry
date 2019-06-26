package com.teamwizardry.wizardry.common.item.pearlbelt;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.item.pearlswapping.IPearlStorageHolder;
import kotlin.jvm.functions.Function2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public interface IPearlBelt extends IPearlStorageHolder, INacreProduct.INacreDecayProduct {

	default void addBeltColorProperty(Item item) {
		item.addPropertyOverride(new ResourceLocation("slot"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				IItemHandler handler = getPearls(stack);
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

	default IItemHandler getBeltPearls(ItemStack stack) {
		return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
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

			long lastCast = NBTHelper.getLong(stack, NBTConstants.NBT.LAST_CAST, -1);
			int decayCooldown = NBTHelper.getInt(stack, NBTConstants.NBT.LAST_COOLDOWN, -1);
			long tick = Minecraft.getMinecraft().world.getTotalWorldTime();
			long timeSinceCooldown = tick - lastCast;
			float decayStage = (decayCooldown > 0) ? ((float) timeSinceCooldown) / decayCooldown : 1f;


			float rand = NBTHelper.getFloat(stack, NBTConstants.NBT.RAND, -1);
			float hue = rand < 0 ? (tick / 140f) % 140f : rand;
			float pow = Math.min(1f, Math.max(0f, getQuality(stack)));

			double decaySaturation = (lastCast == -1 || decayCooldown <= 0 || decayStage >= 1f) ? 1f :
					(decayStage < decayCurveDelimiter) ? Math.pow(Math.E, -15 * decayStage) : Math.pow(Math.E, 3 * decayStage - 3);

			float saturation = curveConst * (1 - (float) Math.pow(Math.E, -pow)) * (float) decaySaturation;

			return Color.HSBtoRGB(hue, saturation, 1f);
		};
	}
}
