package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.api.capability.CustomWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemManaOrb extends ItemMod {

	public ItemManaOrb() {
		super("mana_orb");
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new WizardryCapabilityProvider(new CustomWizardryCapability(10000, 10000, 10000, 10000));
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		if (!BaublesSupport.getItem(player, ModItems.HALO).isEmpty()) {
			ItemStack halo = BaublesSupport.getItem(player, ModItems.HALO);

			CapManager haloManager = new CapManager(halo);
			CapManager orbManager = new CapManager(player.getHeldItem(hand));

			if (!haloManager.isManaFull()) {
				haloManager.addMana(orbManager.getMana());

				player.getHeldItem(hand).shrink(1);
				return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
			}
		}
		return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
	}
}
