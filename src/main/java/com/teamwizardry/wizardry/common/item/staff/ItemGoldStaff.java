package com.teamwizardry.wizardry.common.item.staff;

import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.common.item.ItemWizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by Saad on 6/7/2016.
 */
public class ItemGoldStaff extends ItemWizardry implements INacreColorable {

	public ItemGoldStaff() {
		super("gold_staff", "gold_staff", "gold_staff_pearl");
		setMaxStackSize(1);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
		if ((stack == null) || (world == null) || (entityLiving == null)) return;

		SpellStack.runModules(stack, world, entityLiving);
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@NotNull ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		} else {
			player.setActiveHand(hand);
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
	}

	@NotNull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if ((count > 0) && (count < (getMaxItemUseDuration(stack) - 20)) && (player instanceof EntityPlayer)) {
			SpellStack.runModules(stack, ((EntityPlayer) player).world, player);

		}
		// TODO: PARTICLES
//        int betterCount = Math.abs(count - 72000);
//        Circle3D circle = new Circle3D(player.getPositionVector(), player.width + 0.3, 5);
//        for (Vec3d points : circle.getPoints()) {
//            Vec3d target = new Vec3d(player.posX, player.posY + player.getEyeHeight() - 0.3, player.posZ);
//            Arc3D arc = new Arc3D(points, target, (float) 0.9, 20);
//            if (betterCount < arc.getPoints().size()) {
//                Vec3d point = arc.getPoints().get(betterCount);
//                SparkleFX fizz = GlitterFactory.getInstance().createSparkle(player.world, point, 10);
//                fizz.setFadeOut();
//                fizz.setAlpha(0.1f);
//                fizz.setScale(0.3f);
//                fizz.setBlurred();
//            }
//        }
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) return;

		colorableOnUpdate(stack);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		if (!entityItem.world.isRemote) return false;

		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		NBTTagCompound spell = ItemNBTHelper.getCompound(stack, Constants.NBT.SPELL, true);
		if (spell == null) return;
		TooltipHelper.addToTooltip(tooltip, Wizardry.MODID + ".misc.spell");
		addInformation(spell, tooltip, 0);
	}

	private void addInformation(NBTTagCompound compound, List<String> tooltip, int level) {
		// TODO
	}
}
