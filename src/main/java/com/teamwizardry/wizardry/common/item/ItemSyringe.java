package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.common.base.item.ItemMod;
import com.teamwizardry.wizardry.api.WizardManager;
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

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class ItemSyringe extends ItemMod {

	public ItemSyringe() {
		super("syringe", "syringe", "syringe_mana", "syringe_steroid");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (getItemUseAction(stack) == EnumAction.BOW) {
			if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			} else {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.PASS, stack);
			}
		} else return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Nonnull
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

			if (stack.getItemDamage() == 2) {
				player.addPotionEffect(new PotionEffect(ModPotions.STEROID, 500, 1, true, false));
				stack.setItemDamage(0);
			} else if (stack.getItemDamage() == 1) {
				WizardManager.addMana(WizardManager.getMaxMana(player) / 2, player);
				player.setHealth(player.getHealth() - 2);
				stack.setItemDamage(0);
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
