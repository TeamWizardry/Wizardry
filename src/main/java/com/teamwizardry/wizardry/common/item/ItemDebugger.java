package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.api.screwcaps.DataFactory;
import com.teamwizardry.wizardry.api.screwcaps.WizardryData;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.client.Minecraft;
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
		WizardryData data = DataFactory.INSTANCE.getPlayerData(playerIn);
		Minecraft.getMinecraft().thePlayer.sendChatMessage(data.toNBT() + "");
		/*if (!worldIn.isRemote) {
			if (playerIn.isSneaking())
				data.setBloodType(BloodRegistry.HUMANBLOOD);
			else data.setBloodType(BloodRegistry.PYROBLOOD);
			//return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
			//Minecraft.getMinecraft().thePlayer.sendChatMessage(data.getBloodType() + "");
		}
		if (playerIn.isSneaking())
			if (GuiScreen.isCtrlKeyDown()) data.setBurnout(50);
			else data.setBurnout(0);

		else if (GuiScreen.isCtrlKeyDown()) data.setMana(50);
		else data.setMana(0);*/

		//DataFactory.INSTANCE.setPlayerData(playerIn, data);

		// TeleportUtil.teleportToDimension(playerIn, 100, 0, 100, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
