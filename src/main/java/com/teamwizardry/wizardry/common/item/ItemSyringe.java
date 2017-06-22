package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.capability.CapManager;
import com.teamwizardry.wizardry.common.core.DamageSourceMana;
import com.teamwizardry.wizardry.init.ModPotions;
import net.minecraft.client.Minecraft;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Created by LordSaad.
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
				player.addPotionEffect(new PotionEffect(ModPotions.STEROID, 500, 0, true, true));
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
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		if (stack.getItemDamage() == 2) {
			tooltip.add("Will completely saturate your mana bar and deplete your burnout bar for 10 seconds. Severe side-effects included");
			tooltip.add("Feel the burn.");
		} else if (stack.getItemDamage() == 1) {
			tooltip.add("Will fill your mana bar by 30%");
		} else {
			UUID uuid = ItemNBTHelper.getUUID(stack, "uuid");
			String entity = ItemNBTHelper.getString(stack, "entity", null);
			if (uuid != null) {
				EntityPlayer player1 = player.world.getPlayerEntityByUUID(uuid);
				if (player1 != null) tooltip.add(player1.getName());
			}
			if (entity != null) {
				tooltip.add(entity);
			}
		}
	}
}
