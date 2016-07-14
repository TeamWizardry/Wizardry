package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemManaPearl extends Item {

    public ItemManaPearl() {
        setRegistryName("mana_pearl");
        setUnlocalizedName("mana_pearl");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getBlockState(pos).getBlock() instanceof IManaAcceptor) {
            if (playerIn.getHeldItem(hand) != null) {
                if (playerIn.getHeldItem(hand).getItem() == ModItems.PEARL_MANA) {
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setInteger("link_x", pos.getX());
                    compound.setInteger("link_y", pos.getY());
                    compound.setInteger("link_z", pos.getZ());
                    stack.setTagCompound(compound);
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("LINK ESTABLISHED");
                }
            }
        }
        return EnumActionResult.PASS;
    }
}
