package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import kotlin.jvm.functions.Function2;
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
import java.awt.*;

/**
 * Created by Saad on 8/27/2016.
 */
public class ItemJar extends ItemMod implements IItemColorProvider {

	public ItemJar() {
		super("jar", "jar", "jar_fairy", "jar_jam");
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
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		stack.setCount(stack.getCount() - 1);
		if (entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			entityplayer.getFoodStats().addStats(4, 7f);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			entityLiving.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 2, false, false));
		}

		return stack;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack.getItemDamage() == 2) {
			player.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, stack);
		} else {
			if (!world.isRemote) {
				if (player.isSneaking() && (stack.getItemDamage() == 1)) {
					if (ItemNBTHelper.getBoolean(stack, Constants.NBT.FAIRY_INSIDE, false)) {
						ItemNBTHelper.setBoolean(stack, Constants.NBT.FAIRY_INSIDE, false);
						EntityFairy entity = new EntityFairy(world, new Color(ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF)), ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_AGE, 0));
						entity.setPosition(player.posX, player.posY, player.posZ);
						entity.setSad(true);
						world.spawnEntity(entity);
						stack.setItemDamage(0);
					}
				}
			}
			return new ActionResult(EnumActionResult.FAIL, stack);
		}
	}

	@Nullable
	@Override
	public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
		return (stack, tintIndex) -> ((tintIndex == 0) && (stack.getItemDamage() != 0)) ? ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF) : 0xFFFFFF;
	}
}
