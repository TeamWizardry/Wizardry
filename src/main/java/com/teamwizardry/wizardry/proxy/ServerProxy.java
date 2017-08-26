package com.teamwizardry.wizardry.proxy;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by LordSaad.
 */
public class ServerProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Override
	@Optional.Method(modid = "baubles")
	public ItemStack getCape(EntityPlayer player) {
		IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
		for (int i : BaubleType.BODY.getValidSlots()) {
			ItemStack stack1 = inv.getStackInSlot(i);
			if (stack1.getItem() == ModItems.CAPE) {
				return stack1;
			}
		}
		return null;
	}
}
