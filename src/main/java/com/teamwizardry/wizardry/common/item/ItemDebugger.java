package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.structure.Structure;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants.NBT;
import com.teamwizardry.wizardry.api.block.IStructure;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import com.teamwizardry.wizardry.common.entity.EntityUnicorn;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.Template;
import org.jetbrains.annotations.NotNull;

public class ItemDebugger extends ItemWizardry implements IGlowOverlayable {

	public ItemDebugger() {
		super("debugger");
		setMaxStackSize(1);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, NBT.TAG_OVERLAY), GlowingOverlayHelper.OVERLAY_OVERRIDE);
	}

	@NotNull
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		/*if (playerIn.isSneaking()) {
			if (GuiScreen.isCtrlKeyDown()) {
				WizardryCapabilityProvider.get(playerIn).setMana(10, playerIn);
			} else {
				WizardryCapabilityProvider.get(playerIn).setMana(0, playerIn);
			}
			return EnumActionResult.PASS;
		}*/

		ItemStack cape = playerIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		if (cape != null) {
			ItemNBTHelper.setInt(cape, "time", ItemNBTHelper.getInt(cape, "time", 0) + 100);
			Minecraft.getMinecraft().player.sendChatMessage(ItemNBTHelper.getInt(cape, "time", 0) + "");
		}
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile == null) {
			if (!worldIn.isRemote)
				if (GuiScreen.isShiftKeyDown()) {
					EntityFairy entity = new EntityFairy(worldIn);
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					worldIn.spawnEntity(entity);
				} else {
					EntityUnicorn entity = new EntityUnicorn(worldIn);
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					worldIn.spawnEntity(entity);
				}
			return EnumActionResult.FAIL;
		} else {

			if (tile instanceof IStructure) {

				Structure structure = ModStructures.INSTANCE.structures.get(((IStructure) tile).structureName());

				for (Template.BlockInfo info : structure.blockInfos()) {
					BlockPos newPos = info.pos.add(pos).subtract(new Vec3i(6, 3, 6));
					if (worldIn.getBlockState(newPos).getBlock() != Blocks.AIR) continue;
					if (info.blockState == null) continue;

					boolean flag = false;
					if (!playerIn.isCreative())
						for (ItemStack invStack : playerIn.inventory.mainInventory)
							if (invStack != null
									&& invStack.isItemEqual(new ItemStack(info.blockState.getBlock()))) {
								flag = true;
								invStack.stackSize--;
								break;
							}
					if (!flag && !playerIn.isCreative()) continue;
					worldIn.setBlockState(newPos, info.blockState, 3);
					break;
				}
			}

			if (tile instanceof TileManaBattery) {
				TileManaBattery tmb = (TileManaBattery) worldIn.getTileEntity(pos);
				if (tmb != null)
					playerIn.sendMessage(new TextComponentString("Mana: " + tmb.currentMana + '/' + tmb.maxMana));
			}
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
