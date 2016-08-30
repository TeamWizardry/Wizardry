package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by Saad on 8/27/2016.
 */
public class ItemJar extends ItemWizardry implements IItemColorProvider {

	public ItemJar() {
		super("jar", "jar", "jar_fairy", "jar_jam");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (!worldIn.isRemote) {
			if (playerIn.isSneaking() && itemStackIn.getItemDamage() == 1) {
				if (ItemNBTHelper.getBoolean(itemStackIn, "fairy_inside", false)) {
					ItemNBTHelper.setBoolean(itemStackIn, "fairy_inside", false);
					EntityFairy entity = new EntityFairy(worldIn, new Color(ItemNBTHelper.getInt(itemStackIn, "fairy_color", 0xFFFFFF)), ItemNBTHelper.getInt(itemStackIn, "fairy_age", 0));
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					entity.setSad(true);
					worldIn.spawnEntityInWorld(entity);
					itemStackIn.setItemDamage(0);
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}

	@Nullable
	@SideOnly(Side.CLIENT)
	@Override
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> {
			if (tintIndex == 0 && stack.getItemDamage() != 0) return ItemNBTHelper.getInt(stack, "fairy_color", 0xFFFFFF);
			else return 0xFFFFFF;
		};
	}
}
