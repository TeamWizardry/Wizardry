package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.ModCreativeTab;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.gods.EntityGavreel;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemMagicWand extends ItemMod implements IGlowOverlayable {

	public ItemMagicWand() {
		super("magic_wand");
		setMaxStackSize(1);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, NBT.TAG_OVERLAY), GlowingOverlayHelper.OVERLAY_OVERRIDE);
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer entityPlayer, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (entityPlayer.isSneaking()) {
			if (GuiScreen.isCtrlKeyDown()) {
				WizardryCapabilityProvider.get(entityPlayer).setMana(10, entityPlayer);
			} else {
				WizardryCapabilityProvider.get(entityPlayer).setMana(0, entityPlayer);
			}
		}

		ItemStack cape = entityPlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape.getItem() == ModItems.CAPE) {
			ItemNBTHelper.setInt(cape, "time", ItemNBTHelper.getInt(cape, "time", 0) + 100);
			Minecraft.getMinecraft().player.sendChatMessage(ItemNBTHelper.getInt(cape, "time", 0) + "");
		}

		IBlockState state = world.getBlockState(pos);
		if (!(state.getBlock() instanceof IStructure)) {
			if (!world.isRemote)
				if (GuiScreen.isShiftKeyDown()) {
					EntityFairy entity = new EntityFairy(world);
					entity.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
					world.spawnEntity(entity);
				} else {
					EntityGavreel entity = new EntityGavreel(world);
					entity.setPosition(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ);
					world.spawnEntity(entity);
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


	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	@Nullable
	@Override
	public ModCreativeTab getCreativeTab() {
		return Wizardry.tab;
	}
}
