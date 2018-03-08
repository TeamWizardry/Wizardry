package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
public class ItemSyringe extends ItemMod {

	public ItemSyringe() {
		super("syringe", "syringe", "syringe_mana", "syringe_steroid", "syringe_blood");
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
		return stack.getItemDamage() != 3 ? EnumAction.BOW : EnumAction.NONE;
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
				player.addPotionEffect(new PotionEffect(ModPotions.STEROID, 500, 0, true, false));
				stack.setItemDamage(0);
			} else if (stack.getItemDamage() == 1) {
				CapManager manager = new CapManager(player);
				manager.addMana(manager.getMaxMana() / 2);
				player.attackEntityFrom(DamageSourceMana.INSTANCE, 2);
				stack.setItemDamage(0);
			} else if (stack.getItemDamage() == 0) {
				player.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player), 2);
				stack.setItemDamage(3);
				ItemNBTHelper.setUUID(stack, "uuid", player.getUniqueID());
			}
		}
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		if (stack.getItemDamage() == 0) {
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), 2);
			stack.setItemDamage(3);
			if (entity instanceof EntityPlayer)
				ItemNBTHelper.setUUID(stack, "uuid", entity.getUniqueID());
			else ItemNBTHelper.setString(stack, "entity", entity.getName());
		}
		return false;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String desc = stack.getUnlocalizedName() + ".desc";
		String used = LibrarianLib.PROXY.canTranslate(desc) ? desc : desc + "0";
		if (LibrarianLib.PROXY.canTranslate(used)) {
			TooltipHelper.addToTooltip(tooltip, used);
			int i = 0;
			while (LibrarianLib.PROXY.canTranslate(desc + (++i)))
				TooltipHelper.addToTooltip(tooltip, desc + i);
		}

		if (stack.getItemDamage() == 3 && stack.hasTagCompound()) {
			UUID uuid = ItemNBTHelper.getUUID(stack, "uuid");
			String entity = ItemNBTHelper.getString(stack, "entity", null);
			if (uuid != null) {
				EntityPlayer player1 = null;
				if (worldIn != null) {
					player1 = worldIn.getPlayerEntityByUUID(uuid);
				}
				if (player1 != null) tooltip.add(player1.getName());
			}
			if (entity != null) {
				tooltip.add(entity);
			}
		}
	}
}
