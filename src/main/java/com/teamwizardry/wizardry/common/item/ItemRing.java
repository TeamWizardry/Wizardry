package com.teamwizardry.wizardry.common.item;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.Module;

/**
 * Created by Saad on 6/13/2016.
 */
public class ItemRing extends Item {

    public ItemRing() {
        setRegistryName("ring");
        setUnlocalizedName("ring");
        GameRegistry.register(this);
        setCreativeTab(Wizardry.tab);
    }

    public static int intColor(int r, int g, int b) {
        return (r * 65536 + g * 256 + b);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelResourceLocation full = new ModelResourceLocation(getRegistryName() + "_pearl", "inventory");
        ModelResourceLocation empty = new ModelResourceLocation(getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, empty);
        ModelLoader.setCustomModelResourceLocation(this, 1, full);
    }

    private void setDefaultColor(ItemStack stack, int min, int max) {
        Color color = new Color(ThreadLocalRandom.current().nextInt(min, max), ThreadLocalRandom.current().nextInt(min, max), ThreadLocalRandom.current().nextInt(min, max));
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("red", color.getRed());
        compound.setInteger("green", color.getGreen());
        compound.setInteger("blue", color.getBlue());
        compound.setBoolean("checkRed", false);
        compound.setBoolean("checkBlue", false);
        compound.setBoolean("checkGreen", false);
        stack.setTagCompound(compound);
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
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
    {
    	NBTTagCompound compound = stack.getTagCompound();
    	if (compound == null) return stack;
    	NBTTagCompound spell = compound.getCompoundTag("Spell");
    	String moduleClass = spell.getString(Module.CLASS);
    	Module module = Wizardry.moduleList.modules.get(moduleClass).construct();
    	module.cast((EntityPlayer) entityLiving, entityLiving, spell);
    	return stack;
    }
    
    @Override
    public boolean canItemEditBlocks() {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
        return slotChanged;
    }

    public static class ColorHandler implements IItemColor {
        public ColorHandler() {
        }

        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            if (stack.hasTagCompound()) {
                if (tintIndex == 1) {
                    int r = stack.getTagCompound().getInteger("red3");
                    int g = stack.getTagCompound().getInteger("green3");
                    int b = stack.getTagCompound().getInteger("blue3");
                    return intColor(r, g, b);
                }
            }
            return intColor(255, 255, 255);
        }
    }
}
