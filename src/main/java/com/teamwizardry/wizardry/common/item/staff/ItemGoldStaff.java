package com.teamwizardry.wizardry.common.item.staff;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.IColorable;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;

/**
 * Created by Saad on 6/7/2016.
 */
public class ItemGoldStaff extends Item implements IColorable {

    public ItemGoldStaff() {
        setRegistryName("gold_staff");
        setUnlocalizedName("gold_staff");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
    }

    private static int intColor(int r, int g, int b) {
        return (r * 65536 + g * 256 + b);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
    {
    	NBTTagCompound compound = stack.getTagCompound();
    	if (compound == null) return;
    	NBTTagCompound spell = compound.getCompoundTag("Spell");
    	if (spell == null) return;
    	SpellCastEvent event = new SpellCastEvent(spell, entityLiving, (EntityPlayer) entityLiving);
    	MinecraftForge.EVENT_BUS.post(event);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
    {
    	player.setActiveHand(hand);
    	return new ActionResult<>(EnumActionResult.PASS, stack);
    }
    
    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
    	return EnumAction.BOW;
    }
    
    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
    	return 72000;
    }
    
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation full = new ModelResourceLocation(getRegistryName() + "_pearl", "inventory");
        ModelResourceLocation empty = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, empty);
        ModelLoader.setCustomModelResourceLocation(this, 1, full);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        int max = 220, min = 120;
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            if (compound.hasKey("red") && compound.hasKey("green") && compound.hasKey("blue")) {

                int red = compound.getInteger("red");
                int green = compound.getInteger("green");
                int blue = compound.getInteger("blue");
                boolean checkRed = compound.getBoolean("checkRed");
                boolean checkGreen = compound.getBoolean("checkGreen");
                boolean checkBlue = compound.getBoolean("checkBlue");

                if (checkRed && red < max) red++;
                else if (red > min) red--;
                else green++;

                if (checkGreen && green < max) green++;
                else if (green > min) green--;
                else green++;

                if (checkBlue && blue < max) blue++;
                else if (blue > min) blue--;
                else blue++;

                if (itemRand.nextInt(100) == 0) checkRed = !checkRed;
                if (itemRand.nextInt(100) == 0) checkGreen = !checkGreen;
                if (itemRand.nextInt(100) == 0) checkBlue = !checkBlue;

                compound.setInteger("red", red);
                compound.setInteger("green", green);
                compound.setInteger("blue", blue);
                compound.setBoolean("checkRed", checkRed);
                compound.setBoolean("checkBlue", checkGreen);
                compound.setBoolean("checkGreen", checkBlue);

            } else setDefaultColor(stack, min, max);
        } else setDefaultColor(stack, min, max);
    }

    @Override
    public boolean canItemEditBlocks() {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
        return slotChanged;
    }

    @SideOnly(Side.CLIENT)
    public static class ColorHandler implements IItemColor {
        public ColorHandler() {
        }

        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            if (stack.hasTagCompound()) {
                int r = stack.getTagCompound().getInteger("red");
                int g = stack.getTagCompound().getInteger("green");
                int b = stack.getTagCompound().getInteger("blue");
                return intColor(r, g, b);
            }
            return intColor(255, 255, 255);
        }
    }
}
