package com.teamwizardry.wizardry.common.item.pearl;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.Explodable;
import com.teamwizardry.wizardry.api.item.Infusable;
import com.teamwizardry.wizardry.common.item.ItemWizardry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Created by Saad on 6/10/2016.
 */
public class ItemQuartzPearl extends ItemWizardry implements Infusable, Explodable {

	public ItemQuartzPearl() {
		super("quartz_pearl");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (worldIn.isRemote) {
			for (int i = 0; i < 10; i++) {
				Wizardry.proxy.spawnParticleMagicBurst(worldIn, playerIn.posX + ((Math.random() - 0.5) * 5), playerIn.posY + ((Math.random() - 0.5) * 10), playerIn.posZ + ((Math.random() - 0.5) * 5));
			}
		}
		return super.onItemRightClick(itemStackIn, worldIn, playerIn, hand);
	}
}
