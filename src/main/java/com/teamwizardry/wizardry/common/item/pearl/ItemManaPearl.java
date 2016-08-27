package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.block.IManaAcceptor;
import com.teamwizardry.wizardry.common.item.ItemWizardry;
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
public class ItemManaPearl extends ItemWizardry {

	public ItemManaPearl() {
		super("mana_pearl");
		setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (stack != null && world.getBlockState(pos).getBlock() instanceof IManaAcceptor) {
			ItemNBTHelper.setInt(stack, "link_x", pos.getX());
			ItemNBTHelper.setInt(stack, "link_y", pos.getY());
			ItemNBTHelper.setInt(stack, "link_z", pos.getZ());
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
