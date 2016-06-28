package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/12/2016.
 */
public class ItemPhysicsBook extends Item {

    public ItemPhysicsBook() {
        setRegistryName("physics_book");
        setUnlocalizedName("physics_book");
        GameRegistry.register(this);
        setCreativeTab(Wizardry.tab);
    }

    public static ItemStack getHeldBook(EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack == null || !(stack.getItem() instanceof ItemPhysicsBook)) {
            stack = player.getHeldItemOffhand();
        }
        if (stack == null || !(stack.getItem() instanceof ItemPhysicsBook)) {
            return null;
        }
        return stack;
    }

    public static String getHeldPath(EntityPlayer player) {
        ItemStack stack = getHeldBook(player);
        String path = "/";
        if (stack != null) {
            path = ((ItemPhysicsBook) stack.getItem()).getGuide(stack);
        }
        return path;
    }

    public static int getHeldPage(EntityPlayer player) {
        ItemStack stack = getHeldBook(player);
        int page = 0;
        if (stack != null) {
            page = ((ItemPhysicsBook) stack.getItem()).getPage(stack);
        }
        return page;
    }

    public static void setHeldPath(EntityPlayer player, String path) {
        ItemStack stack = getHeldBook(player);
        if (stack != null) {
            ((ItemPhysicsBook) stack.getItem()).setGuide(stack, path);
        }
    }

    public static void setHeldPage(EntityPlayer player, int page) {
        ItemStack stack = getHeldBook(player);
        if (stack != null) {
            ((ItemPhysicsBook) stack.getItem()).setPage(stack, page);
        }
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        playerIn.openGui(Wizardry.instance, Constants.PageNumbers.GUIDE, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
        return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
    }

    public String getGuide(ItemStack stack) {
        if (!stack.hasTagCompound())
            return "/";
        return stack.getTagCompound().getString("path");
    }

    public void setGuide(ItemStack stack, String guide) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString("path", guide);
    }

    public int getPage(ItemStack stack) {
        if (!stack.hasTagCompound())
            return 0;
        return stack.getTagCompound().getInteger("page");
    }

    public void setPage(ItemStack stack, int page) {
        if (!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger("page", page);
    }

}
