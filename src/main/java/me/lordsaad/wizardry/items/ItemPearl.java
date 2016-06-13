package me.lordsaad.wizardry.items;

import me.lordsaad.wizardry.SerializableItemStack;
import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

/**
 * Created by Saad on 6/10/2016.
 */
public class ItemPearl extends Item {

    public ItemPearl() {
        setRegistryName("pearl");
        setUnlocalizedName("pearl");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
    }

    public static int intColor(int r, int g, int b) {
        return (r * 65536 + g * 256 + b);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!stack.hasTagCompound()) {
            NBTTagCompound compound = new NBTTagCompound();
            int initialR = itemRand.nextInt(255);
            int initialG = itemRand.nextInt(255);
            int initialB = itemRand.nextInt(255);
            compound.setInteger("red1", initialR);
            compound.setInteger("green1", initialG);
            compound.setInteger("blue1", initialB);
            compound.setInteger("red3", initialR);
            compound.setInteger("green3", initialG);
            compound.setInteger("blue3", initialB);
            compound.setInteger("red2", itemRand.nextInt(255));
            compound.setInteger("green2", itemRand.nextInt(255));
            compound.setInteger("blue2", itemRand.nextInt(255));
            compound.setInteger("ticker", 0);
            compound.setDouble("steps", 0);
            compound.setString("type", "mundane");
            stack.setTagCompound(compound);
        }

        if (stack.hasTagCompound()) {
            if (stack.getTagCompound().getString("type").equals("infused")) {
                int ticker = stack.getTagCompound().getInteger("ticker");
                if (ticker >= 30) {
                    if (stack.getTagCompound().getDouble("steps") <= 30) {
                        stack.getTagCompound().setInteger("ticker", 0);
                        int r1 = stack.getTagCompound().getInteger("red1");
                        int g1 = stack.getTagCompound().getInteger("green1");
                        int b1 = stack.getTagCompound().getInteger("blue1");
                        int r2 = stack.getTagCompound().getInteger("red2");
                        int g2 = stack.getTagCompound().getInteger("green2");
                        int b2 = stack.getTagCompound().getInteger("blue2");

                        double ratio = stack.getTagCompound().getDouble("steps") / 30;
                        int red3 = (int) Math.abs((ratio * r2) + ((1 - ratio) * r1));
                        int green3 = (int) Math.abs((ratio * g2) + ((1 - ratio) * g1));
                        int blue3 = (int) Math.abs((ratio * b2) + ((1 - ratio) * b1));

                        stack.getTagCompound().setInteger("red3", red3);
                        stack.getTagCompound().setInteger("green3", green3);
                        stack.getTagCompound().setInteger("blue3", blue3);
                        stack.getTagCompound().setDouble("steps", stack.getTagCompound().getDouble("steps") + 1);
                    } else {
                        stack.getTagCompound().setDouble("steps", 0);
                        stack.getTagCompound().setInteger("ticker", 0);
                        stack.getTagCompound().setInteger("red2", stack.getTagCompound().getInteger("red1"));
                        stack.getTagCompound().setInteger("blue2", stack.getTagCompound().getInteger("blue1"));
                        stack.getTagCompound().setInteger("green2", stack.getTagCompound().getInteger("green1"));

                        stack.getTagCompound().setInteger("red1", stack.getTagCompound().getInteger("red3"));
                        stack.getTagCompound().setInteger("blue1", stack.getTagCompound().getInteger("blue3"));
                        stack.getTagCompound().setInteger("green1", stack.getTagCompound().getInteger("green3"));
                    }
                } else {
                    ticker++;
                    stack.getTagCompound().setInteger("ticker", ticker);
                }
            } else if (stack.getTagCompound().getString("type").equals("mundane")) {

            } else {

            }
        }
    }

    public void addSpellItems(ItemStack stack, ArrayList<ItemStack> items) {
        NBTTagCompound compound = new NBTTagCompound();
        int i = 0;
        for (ItemStack item : items) {
            compound = new SerializableItemStack(item).writeToNBT("item" + i++, compound);
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
                if (tintIndex == 0) {
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
