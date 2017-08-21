package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.advancement.IPickupAchievement;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemDevilDust extends ItemMod implements IPickupAchievement {

	public ItemDevilDust() {
		super("devil_dust");
	}

	@Override
	public Advancement getAdvancementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getAdvancementManager().getAdvancement(new ResourceLocation(Wizardry.MODID, "advancements/advancement_devildust.json"));
	}
}
