package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.bloods.BloodRegistry;
import com.teamwizardry.wizardry.api.bloods.IBloodType;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.api.save.WizardryDataHandler;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDebugger extends Item implements IGlowOverlayable {

	public ItemDebugger() {
		setRegistryName("debugger");
		setUnlocalizedName("debugger");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, "overlay"), GlowingOverlayHelper.OVERLAY_OVERRIDE);
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
		if (!worldIn.isRemote) {
			if (playerIn.isSneaking())
				WizardryDataHandler.setBloodType(playerIn, null);
			else {
				IBloodType type = WizardryDataHandler.getBloodType(playerIn);
				int i = type == null ? 0 : (BloodRegistry.getBloodTypeId(type) + 1) % BloodRegistry.getRegistry().size();
				WizardryDataHandler.setBloodType(playerIn, BloodRegistry.getBloodTypeById(i));
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
		}
		if (playerIn.isSneaking())
			if (GuiScreen.isCtrlKeyDown())
				WizardryDataHandler.setBurnoutAmount(playerIn, 50);
			else
				WizardryDataHandler.setBurnoutAmount(playerIn, 0);
		else if (GuiScreen.isCtrlKeyDown()) WizardryDataHandler.setMana(playerIn, 50);
		else WizardryDataHandler.setMana(playerIn, 0);

		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
