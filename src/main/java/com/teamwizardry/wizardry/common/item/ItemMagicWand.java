package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntitySpiritWight;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemMagicWand extends ItemWizardry implements IGlowOverlayable {

	public ItemMagicWand() {
		super("magic_wand");
		setMaxStackSize(1);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, NBT.TAG_OVERLAY), GlowingOverlayHelper.OVERLAY_OVERRIDE);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (playerIn.isSneaking()) {
			if (GuiScreen.isCtrlKeyDown()) {
				WizardryCapabilityProvider.get(playerIn).setMana(10, playerIn);
			} else {
				WizardryCapabilityProvider.get(playerIn).setMana(0, playerIn);
			}
		}

		ItemStack cape = playerIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape != null) {
			ItemNBTHelper.setInt(cape, "time", ItemNBTHelper.getInt(cape, "time", 0) + 100);
			Minecraft.getMinecraft().player.sendChatMessage(ItemNBTHelper.getInt(cape, "time", 0) + "");
		}
		IBlockState state = worldIn.getBlockState(pos);
		if (!(state.getBlock() instanceof IStructure)) {
			if (!worldIn.isRemote)
				if (GuiScreen.isShiftKeyDown()) {
					EntityFairy entity = new EntityFairy(worldIn);
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					worldIn.spawnEntity(entity);
				} else {
					EntitySpiritWight entity = new EntitySpiritWight(worldIn);
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					worldIn.spawnEntity(entity);
				}
			return EnumActionResult.FAIL;
		} else {

			/*if (tile instanceof TileManaBattery) {
				TileManaBattery tmb = (TileManaBattery) worldIn.getTileEntity(pos);
				if (tmb != null)
					playerIn.sendMessage(new TextComponentString("Mana: " + tmb.currentMana + '/' + tmb.maxMana));
			}*/
		}
		return EnumActionResult.PASS;
	}


	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);


		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}
}
