package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by Saad on 8/27/2016.
 */
public class ItemJar extends ItemWizardry implements IItemColorProvider {

    public ItemJar() {
        super("jar", "jar", "jar_fairy", "jar_jam");
        setMaxStackSize(1);
    }

    @NotNull
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
    public ItemStack onItemUseFinish(@NotNull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        --stack.stackSize;
        if (entityLiving instanceof EntityPlayer)
            ((EntityPlayer) entityLiving).getFoodStats().addStats(4, 7f);
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityLiving;
            worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            entityLiving.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 2, false, false));
        }

        return stack;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (itemStackIn.getItemDamage() == 2) {
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        } else {
            if (!worldIn.isRemote) {
                if (playerIn.isSneaking() && (itemStackIn.getItemDamage() == 1)) {
                    if (ItemNBTHelper.getBoolean(itemStackIn, Constants.NBT.FAIRY_INSIDE, false)) {
                        ItemNBTHelper.setBoolean(itemStackIn, Constants.NBT.FAIRY_INSIDE, false);
                        EntityFairy entity = new EntityFairy(worldIn, new Color(ItemNBTHelper.getInt(itemStackIn, Constants.NBT.FAIRY_COLOR, 0xFFFFFF)), ItemNBTHelper.getInt(itemStackIn, Constants.NBT.FAIRY_AGE, 0));
                        entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
                        entity.setSad(true);
                        worldIn.spawnEntity(entity);
                        itemStackIn.setItemDamage(0);
                    }
                }
            }
            return new ActionResult(EnumActionResult.FAIL, itemStackIn);
        }
    }

    @Nullable
    @Override
    public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
        return (stack, tintIndex) -> ((tintIndex == 0) && (stack.getItemDamage() != 0)) ? ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF) : 0xFFFFFF;
    }
}
