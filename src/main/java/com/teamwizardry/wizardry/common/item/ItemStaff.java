package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.IContinuousModule;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeTouch;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
		if (isCoolingDown(stack)) return false;

		if (BaublesSupport.getItem(playerIn, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return false;

		boolean touch = false;
		for (Module module : SpellUtils.getModules(stack)) {
			if (module instanceof ModuleShapeTouch) {
				touch = true;
				break;
			}
		}

		if (!touch) return false;

		SpellData spell = new SpellData(playerIn.world);
		spell.processEntity(playerIn, true);
		spell.processEntity(target, false);
		SpellUtils.runSpell(stack, spell);

		setCooldown(playerIn.world, playerIn, hand, stack, spell);
		return true;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9, float par10) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.isSneaking()) {
			for (Module module : SpellUtils.getAllModules(stack)) {
				if (module instanceof IBlockSelectable) {
					player.getEntityData().setTag("selected", NBTUtil.writeBlockState(new NBTTagCompound(), world.getBlockState(pos)));
					player.swingArm(hand);
					return EnumActionResult.PASS;
				}
			}
		}
		if (isCoolingDown(stack)) return EnumActionResult.PASS;

		if (BaublesSupport.getItem(player, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return EnumActionResult.PASS;

		boolean isOnTouch = false;
		for (Module module : SpellUtils.getModules(stack))
			if (module instanceof ModuleShapeTouch) {
				isOnTouch = true;
				break;
			}

		if (!isOnTouch) return EnumActionResult.PASS;

		SpellData spell = new SpellData(world);
		spell.processEntity(player, true);
		spell.processBlock(pos, side, new Vec3d(pos).addVector(0.5, 0.5, 0.5));
		SpellUtils.runSpell(stack, spell);

		setCooldown(world, player, hand, stack, spell);

		return EnumActionResult.PASS;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (player.isSneaking()) {
			for (Module module : SpellUtils.getAllModules(stack)) {
				if (module instanceof IBlockSelectable) {
					return new ActionResult<>(EnumActionResult.PASS, stack);
				}
			}
		}

		if (getItemUseAction(stack) == EnumAction.NONE) {
			if (!isCoolingDown(stack)) {

				if (BaublesSupport.getItem(player, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
					return new ActionResult<>(EnumActionResult.FAIL, stack);

				SpellData spell = new SpellData(world);
				spell.processEntity(player, true);
				SpellUtils.runSpell(stack, spell);

				player.swingArm(EnumHand.MAIN_HAND);
				setCooldown(world, player, hand, stack, spell);
			}
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
		boolean anyNotContinuous = false;
		for (Module module : SpellUtils.getModules(stack))
			if (!(module instanceof IContinuousModule && module.getChargeupTime() <= 0)) {
				anyNotContinuous = true;
				break;
			}
		return anyNotContinuous ? EnumAction.NONE : EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		int maxChargeUp = 0;
		boolean anyNotContinuous = false;
		for (Module module : SpellUtils.getModules(stack)) {
			if (!(module instanceof IContinuousModule)) {
				anyNotContinuous = true;
				if (module.getChargeupTime() > maxChargeUp) maxChargeUp = module.getChargeupTime();
			}
		}

		return anyNotContinuous ? maxChargeUp : 72000;
	}

	@Nonnull
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		return EnumActionResult.PASS;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
		if (!(player instanceof EntityPlayer)) return;

		if (BaublesSupport.getItem(player, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return;

		boolean isContinuous = false;
		for (Module module : SpellUtils.getModules(stack))
			if (module instanceof IContinuousModule) {
				isContinuous = true;
				break;
			}

		if (!isContinuous && count > 1) return;

		SpellData spell = new SpellData(player.world);
		spell.processEntity(player, true);
		spell.processEntity(player, false);
		SpellUtils.runSpell(stack, spell);

		if (!isContinuous) {
			player.swingArm(player.getActiveHand());
			setCooldown(player.world, (EntityPlayer) player, player.getActiveHand(), stack, spell);
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);
		if (entityIn instanceof EntityPlayer)
			updateCooldown(stack);
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
		ArrayList<Module> modules = SpellUtils.getModules(stack);
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
	public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
		ArrayList<Module> modules = SpellUtils.getModules(stack);
		Module lastModule = null;
		for (Module module : modules) {
			if (lastModule == null) lastModule = module;
			if (module != null) {
				if (module != lastModule) tooltip.add("");
				//tooltip.add("Final " + TextFormatting.BLUE + "Mana" + TextFormatting.GRAY + "/" + TextFormatting.RED + "Burnout" + TextFormatting.GRAY + " Cost: " + TextFormatting.BLUE + module.finalManaDrain + TextFormatting.GRAY + "/" + TextFormatting.RED + module.finalBurnoutFill);
				Module tempModule = module;
				int i = 0;
				while (tempModule != null) {
					tooltip.add(StringUtils.repeat("-", i) + "> " + TextFormatting.GRAY + tempModule.getReadableName() + " - " + TextFormatting.BLUE + (int) Math.round(tempModule.getManaDrain() * tempModule.getMultiplier()) + TextFormatting.GRAY + "/" + TextFormatting.RED + (int) Math.round(tempModule.getBurnoutFill() * tempModule.getMultiplier()));
					if (GuiScreen.isShiftKeyDown()) {
						for (String key : tempModule.attributes.getKeySet())
							tooltip.add(StringUtils.repeat(" ", i + 1) + " | " + TextFormatting.DARK_GRAY + key + " x" + (int) Math.round(tempModule.attributes.getDouble(key)));
					}
					tempModule = tempModule.nextModule;
					i++;
				}
			}
		}

		if (!GuiScreen.isShiftKeyDown() && !modules.isEmpty()) {
			tooltip.add(TextFormatting.GRAY + "<- " + TextFormatting.DARK_GRAY + "Shift for more info" + TextFormatting.GRAY + " ->");
		}
	}

	@Override
	public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab))
			subItems.add(new ItemStack(this));
	}
}
