package com.teamwizardry.wizardry.items.pearls;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.IExplodable;
import com.teamwizardry.wizardry.particles.SparkleFX;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Saad on 6/28/2016.
 */
public class ItemNacrePearl extends Item implements IExplodable {

    private List<Integer> potions = new ArrayList<>();

    public ItemNacrePearl() {
        setRegistryName("nacre_pearl");
        setUnlocalizedName("nacre_pearl");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
        addPotions();
    }

    private static int intColor(int r, int g, int b) {
        return (r * 65536 + g * 256 + b);
    }

    private void addPotions() {
        potions.add(1);
        potions.add(3);
        potions.add(5);
        potions.add(8);
        potions.add(11);
        potions.add(12);
        potions.add(21);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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

    public void addSpellItems(ItemStack stack, ArrayList<ItemStack> items) {
        NBTTagCompound compound = new NBTTagCompound();
        if (items.size() > 0) {
            NBTTagList list = new NBTTagList();
            for (ItemStack anInventory : items)
                list.appendTag(anInventory.writeToNBT(new NBTTagCompound()));
            compound.setTag("inventory", list);
        }
        compound.setString("type", String.valueOf("infused"));
        stack.setTagCompound(compound);
    }

    public String getPearlType(ItemStack stack) {
        if (stack.hasTagCompound())
            if (stack.getTagCompound().hasKey("type"))
                return stack.getTagCompound().getString("type");
            else return "mundane";
        else return "mundane";
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldS, ItemStack newS, boolean slotChanged) {
        return slotChanged;
    }

    public void explode(Entity entityIn) {
        Random rand = new Random();
        int range = 5;
        List<EntityLivingBase> entitys = entityIn.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(entityIn.posX - range, entityIn.posY - range, entityIn.posZ - range, entityIn.posX + range, entityIn.posY + range, entityIn.posZ + range));
        for (EntityLivingBase e : entitys)
            e.addPotionEffect(new PotionEffect(Potion.getPotionById(potions.get(rand.nextInt(potions.size()))), rand.nextInt(30) * 20, rand.nextInt(2) + 1));

        for (int i = 0; i < 300; i++) {
            SparkleFX fizz = Wizardry.proxy.spawnParticleSparkle(entityIn.worldObj, entityIn.posX, entityIn.posY + 0.5, entityIn.posZ, 1, 1F, 30, false);
            fizz.jitter(10, 0.1, 0.1, 0.1);
            fizz.randomDirection(0.3, 0.3, 0.3);
        }
    }

    @Override
    public boolean canItemEditBlocks() {
        return false;
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