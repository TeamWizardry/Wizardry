package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.block.ItemModBlock;
import com.teamwizardry.librarianlib.features.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import kotlin.jvm.functions.Function2;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Demoniaque on 8/27/2016.
 */
public class ItemJar extends ItemModBlock implements IItemColorProvider {

	public ItemJar(Block block) {
		super(block);
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		if (stack.getItemDamage() == 2) return EnumAction.DRINK;
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (entity instanceof EntityFairy) {
			EntityFairy fairy = (EntityFairy) entity;
			ItemNBTHelper.setBoolean(stack, Constants.NBT.FAIRY_INSIDE, true);
			ItemNBTHelper.setInt(stack, Constants.NBT.FAIRY_COLOR, fairy.getColor().getRGB());
			ItemNBTHelper.setInt(stack, Constants.NBT.FAIRY_AGE, fairy.getAge());
			entity.world.removeEntity(entity);
			return true;
		}
		return false;
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		stack.setCount(stack.getCount() - 1);
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			entityplayer.getFoodStats().addStats(4, 7f);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 1, true, false));
		}

		return stack;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() == 2) {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> ((tintIndex == 0) && (stack.getItemDamage() != 0)) ? ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF) : 0xFFFFFF;
	}
}
