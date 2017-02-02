package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Created by LordSaad.
 */
public class ItemManaSteroid extends ItemWizardry {

	public ItemManaSteroid() {
		super("mana_steroid", "mana_steroid", "mana_steroid_full");
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
			((EntityPlayer) player).getCooldownTracker().setCooldown(this, 500);
			IWizardryCapability cap = WizardryCapabilityProvider.get((EntityPlayer) player);
			cap.setMana(cap.getMaxMana(), (EntityPlayer) player);
			cap.setBurnout(0, (EntityPlayer) player);
		}
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (entityItem.isInsideOfMaterial(ModBlocks.MANA_MATERIAL))
			entityItem.getEntityItem().setItemDamage(1);
		return super.onEntityItemUpdate(entityItem);
	}
}
