package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.IItemColorProvider;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.common.entity.EntityFairy;
import kotlin.jvm.functions.Function2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!worldIn.isRemote) {
			if (playerIn.isSneaking() && (itemStackIn.getItemDamage() == 1)) {
				if (ItemNBTHelper.getBoolean(itemStackIn, Constants.NBT.FAIRY_INSIDE, false)) {
					ItemNBTHelper.setBoolean(itemStackIn, Constants.NBT.FAIRY_INSIDE, false);
					EntityFairy entity = new EntityFairy(worldIn, new Color(ItemNBTHelper.getInt(itemStackIn, Constants.NBT.FAIRY_COLOR, 0xFFFFFF)), ItemNBTHelper.getInt(itemStackIn, Constants.NBT.FAIRY_AGE, 0));
					entity.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
					entity.setSad(true);
                    worldIn.spawnEntity(entity);
                    itemStackIn.setItemDamage(0);
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}

	@Nullable
	@Override
    public Function2<ItemStack, Integer, Integer> getItemColorFunction() {
        return (stack, tintIndex) -> ((tintIndex == 0) && (stack.getItemDamage() != 0)) ? ItemNBTHelper.getInt(stack, Constants.NBT.FAIRY_COLOR, 0xFFFFFF) : 0xFFFFFF;
	}
}
