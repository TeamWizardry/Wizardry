package com.teamwizardry.wizardry.common.item.dusts;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.common.entity.EntityBomb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemBomb extends ItemMod {

	public ItemBomb() {
		super("bomb", "vortex_bomb", "repulsion_bomb");
		setMaxStackSize(1);
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (!world.isRemote) {
			EntityBomb bomb = new EntityBomb(world, player);
			bomb.setBombItem(stack);
			bomb.setPosition(player.posX, player.posY + player.eyeHeight, player.posZ);
			bomb.shoot(player, player.rotationPitch, player.rotationYaw, 0.0f, 1.5f, 1.0f);
			stack.shrink(1);
			world.spawnEntity(bomb);
			bomb.velocityChanged = true;
		}

		player.getCooldownTracker().setCooldown(this, 20);

		return new ActionResult<>(EnumActionResult.PASS, stack);
	}
}
