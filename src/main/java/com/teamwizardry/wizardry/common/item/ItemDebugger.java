package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.save.WizardryDataHandler;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDebugger extends Item {

    public ItemDebugger() {
        setRegistryName("debugger");
        setUnlocalizedName("debugger");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.getTileEntity(pos) instanceof TileManaBattery) {
            TileManaBattery tmb = (TileManaBattery) worldIn.getTileEntity(pos);
            if (!worldIn.isRemote) {
                playerIn.addChatMessage(new TextComponentString("Mana: " + tmb.current_mana + "/" + tmb.MAX_MANA));
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if(!worldIn.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
        if(playerIn.isSneaking())
            if(GuiScreen.isCtrlKeyDown())
                WizardryDataHandler.setBurnoutAmount(playerIn, 50);
            else
                WizardryDataHandler.setBurnoutAmount(playerIn, 0);
        else
            if(GuiScreen.isCtrlKeyDown())
                WizardryDataHandler.setMana(playerIn, 50);
            else
                WizardryDataHandler.setMana(playerIn, 0);

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
