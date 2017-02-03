package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by LordSaad.
 */
public class ItemSyringe extends ItemWizardry {

	public ItemSyringe() {
		super("syringe", "syringe", "syringe_mana", "syringe_steroid");
		setMaxStackSize(1);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (getItemUseAction(stack) == EnumAction.BOW) {
			if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			} else {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.PASS, stack);
			}
		} else return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@NotNull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return stack.getItemDamage() == 0 ? EnumAction.NONE : EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 60;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!(player instanceof EntityPlayer)) return;
		if (count <= 1) {
			player.swingArm(player.getActiveHand());
			((EntityPlayer) player).getCooldownTracker().setCooldown(this, stack.getItemDamage() == 1 ? 100 : 500);
			IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) player);

			if (stack.getItemDamage() == 2) {
				player.addPotionEffect(new PotionEffect(ModPotions.STEROID, 500, 1, true, false));
			} else if (stack.getItemDamage() == 1) {
				if (cap.getMaxMana() >= (cap.getMana() * 1.3))
					cap.setMana((int) (cap.getMana() * 1.3), (EntityPlayer) player);
				else cap.setMana(cap.getMaxMana(), (EntityPlayer) player);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (stack.getItemDamage() == 2) {
			tooltip.add("Will completely saturate your mana bar and deplete your burnout bar for 10 seconds. Severe side-effects included");
			tooltip.add("Feel the burn.");
		} else if (stack.getItemDamage() == 1) {
			tooltip.add("Will fill your mana bar by 30%");
		}
	}
}
