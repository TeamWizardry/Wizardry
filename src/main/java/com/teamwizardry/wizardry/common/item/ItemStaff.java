package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.*;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeTouch;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 6/7/2016.
 */
public class ItemStaff extends ItemMod implements INacreColorable.INacreDecayColorable, ICooldown {

	public ItemStaff() {
		super("staff", "staff", "staff_pearl");
		setMaxStackSize(1);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (isCoolingDown(stack)) return true;

		boolean touch = false;
		for (Module module : SpellStack.getModules(stack)) {
			if (module instanceof ModuleShapeTouch) {
				touch = true;
				break;
			}
		}

		if (!touch) return true;

		SpellData spell = new SpellData(playerIn.world);
		spell.processEntity(playerIn, true);
		spell.processEntity(target, false);
		SpellStack.runSpell(stack, spell, playerIn);

		setCooldown(stack, hand, playerIn, playerIn.world);
		return true;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9, float par10) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			for (Module module : SpellStack.getAllModules(stack)) {
				if (module instanceof IBlockSelectable) {
					player.getEntityData().setTag("selected", NBTUtil.writeBlockState(new NBTTagCompound(), world.getBlockState(pos)));
					return EnumActionResult.FAIL;
				}
			}
		}
		if (isCoolingDown(stack)) return EnumActionResult.PASS;

		SpellData spell = new SpellData(world);
		spell.processEntity(player, true);
		spell.processBlock(pos, side, new Vec3d(pos).addVector(0.5, 0.5, 0.5));
		SpellStack.runSpell(stack, spell, player);

		setCooldown(stack, hand, player, world);

		return EnumActionResult.PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (getItemUseAction(stack) == EnumAction.NONE) {
			if (!player.isSneaking() && !world.isRemote && !isCoolingDown(stack)) {
				SpellData spell = new SpellData(world);
				spell.processEntity(player, true);
				SpellStack.runSpell(stack, spell, player);
			}
			player.swingArm(EnumHand.MAIN_HAND);
			setCooldown(stack, hand, player, world);
			return new ActionResult<>(EnumActionResult.PASS, stack);
		} else {
			if (world.isRemote && (Minecraft.getMinecraft().currentScreen != null)) {
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			} else {
				player.setActiveHand(hand);
				return new ActionResult<>(EnumActionResult.PASS, stack);
			}
		}
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		for (Module module : SpellStack.getAllModules(stack))
			if (module instanceof IContinuousSpell || module.getChargeupTime() > 0) return EnumAction.BOW;
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		int maxChargeUp = 0;
		for (Module module : SpellStack.getAllModules(stack)) {
			if (module instanceof IContinuousSpell) return 72000;
			if (module.getChargeupTime() > maxChargeUp) maxChargeUp = module.getChargeupTime();
		}

		return maxChargeUp;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return EnumActionResult.PASS;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!(player instanceof EntityPlayer)) return;
		boolean isContinuous = false;
		for (Module module : SpellStack.getAllModules(stack))
			if (module instanceof IContinuousSpell) {
				isContinuous = true;
				break;
			}

		if (!isContinuous && count > 1) return;

		SpellData spell = new SpellData(player.world);
		spell.processEntity(player, true);
		SpellStack.runSpell(stack, spell, player);

		if (!isContinuous) {
			player.swingArm(player.getActiveHand());
			setCooldown(stack, player.getActiveHand(), (EntityPlayer) player, player.world);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);
		if (entityIn instanceof EntityPlayer)
			updateCooldown(stack, (EntityPlayer) entityIn);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		colorableOnEntityItemUpdate(entityItem);

		return super.onEntityItemUpdate(entityItem);
	}

	@Nonnull
	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		StringBuilder finalName = null;
		ArrayList<Module> modules = SpellStack.getModules(stack);
		Module lastModule = null;
		for (Module module : modules) {
			if (lastModule == null) lastModule = module;
			if (module != null) {
				Module tempModule = module;
				while (tempModule != null) {

					boolean next = false;
					if (lastModule != module) {
						lastModule = module;
						finalName.append(" || ");
						next = true;
					}

					if (finalName == null) finalName = new StringBuilder(tempModule.getReadableName());
					else {
						if (!next) finalName.append(" -> ");
						finalName.append(tempModule.getReadableName());
					}

					tempModule = tempModule.nextModule;
				}
			}
		}

		if (finalName == null)
			return ("" + LibrarianLib.PROXY.translate(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
		else return finalName.toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		ArrayList<Module> modules = SpellStack.getModules(stack);
		Module lastModule = null;
		for (Module module : modules) {
			if (lastModule == null) lastModule = module;

			if (module != null) {
				if (module != lastModule) tooltip.add("");
				//tooltip.add("Final " + TextFormatting.BLUE + "Mana" + TextFormatting.GRAY + "/" + TextFormatting.RED + "Burnout" + TextFormatting.GRAY + " Cost: " + TextFormatting.BLUE + module.finalManaDrain + TextFormatting.GRAY + "/" + TextFormatting.RED + module.finalBurnoutFill);
				Module tempModule = module;
				int i = 0;
				while (tempModule != null) {
					tooltip.add(new String(new char[i]).replace("\0", "-") + "> " + TextFormatting.GRAY + tempModule.getReadableName() + " - " + TextFormatting.BLUE + tempModule.getManaDrain() + TextFormatting.GRAY + "/" + TextFormatting.RED + tempModule.getBurnoutFill());
					for (String key : tempModule.attributes.getKeySet())
						tooltip.add(new String(new char[i]).replace("\0", "-") + "^ " + TextFormatting.YELLOW + key + TextFormatting.GRAY + " * " + TextFormatting.GREEN + tempModule.attributes.getDouble(key));
					tempModule = tempModule.nextModule;
					i++;
				}
			}
		}
	}
}
