package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.base.item.ItemModFood;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.wizardry.api.NBTConstants;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/28/2016.
 */
public class ItemFairyImbuedApple extends ItemModFood implements IItemColorProvider {

	public ItemFairyImbuedApple() {
		super("fairy_imbued_apple", 10, 1, false);
		setAlwaysEdible();
		setMaxStackSize(64);
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> NBTHelper.getInt(stack, NBTConstants.NBT.FAIRY_COLOR, 0xFFFFFF);
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		if(!worldIn.isRemote) {
			player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 60*20, 0, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 60*20, 1, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60*20, 0, false, true));
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 3*20, 3, false, true));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
