package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.ICooldown;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.spell.IBlockSelectable;
import com.teamwizardry.wizardry.api.spell.SpellData;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Created by Demoniaque on 6/7/2016.
 */
public class ItemStaff extends ItemMod implements INacreProduct.INacreDecayProduct, ICooldown {

	public ItemStaff() {
		super("staff", "staff", "staff_pearl");
		setMaxStackSize(1);
	}

	@Override
	public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
		if (isCoolingDown(playerIn.world, stack)) return false;

		if (BaublesSupport.getItem(playerIn, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return false;

		if (requiresBowAction(stack)) return false;

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
			for (SpellRing spellRing : SpellUtils.getAllSpellRings(stack)) {
				if (spellRing.getModule() instanceof IBlockSelectable) {
					player.getEntityData().setTag("selected", NBTUtil.writeBlockState(new NBTTagCompound(), world.getBlockState(pos)));
					player.stopActiveHand();
					player.swingArm(hand);
					return EnumActionResult.PASS;
				}
			}
		}

		if (isCoolingDown(world, stack)) return EnumActionResult.PASS;
		if (requiresBowAction(stack)) return EnumActionResult.PASS;
		if (BaublesSupport.getItem(player, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return EnumActionResult.PASS;

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

		boolean hasHalo = BaublesSupport.getItem(player, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty();
		if (isCoolingDown(world, stack) || hasHalo || (world.isRemote && (Minecraft.getMinecraft().currentScreen != null))) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		} else {
			if (requiresBowAction(stack))
				player.setActiveHand(hand);
			else {
				SpellData spell = new SpellData(player.world);
				spell.processEntity(player, true);
				SpellUtils.runSpell(stack, spell);
				setCooldown(world, player, hand, stack, spell);
			}
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
	}

	@Nonnull
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
		if (isCoolingDown(player.world, stack)) return;
		if (!(player instanceof EntityPlayer)) return;

		if (isContinuousSpell(stack)) {

			SpellData spell = new SpellData(player.world);
			spell.processEntity(player, true);
			SpellUtils.runSpell(stack, spell);
		} else {
			int chargeup = getChargeupTime(stack);
			if (72000 - count >= chargeup) {

				SpellData spell = new SpellData(player.world);
				spell.processEntity(player, true);
				SpellUtils.runSpell(stack, spell);

				setCooldown(player.world, (EntityPlayer) player, player.getActiveHand(), stack, spell);
			}
		}
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		colorableOnUpdate(stack, worldIn);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		colorableOnEntityItemUpdate(entityItem);
		return super.onEntityItemUpdate(entityItem);
	}

	@Nonnull
	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		if (!stack.hasTagCompound())
			return super.getItemStackDisplayName(stack);

		StringBuilder finalName = null;
		List<SpellRing> spellChains = SpellUtils.getSpellChains(stack);
		for (SpellRing spellRing : spellChains) {

			if (finalName == null) {
				finalName = new StringBuilder();
			} else finalName.append(" / ");

			finalName.append(spellRing.toString());
		}

		if (finalName == null)
			return super.getItemStackDisplayName(stack);
		else return finalName.toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (!stack.hasTagCompound())
			return;

		List<SpellRing> spellRings = SpellUtils.getSpellChains(stack);
		SpellRing lastRing = null;
		for (SpellRing ring : spellRings) {
			if (lastRing == null) lastRing = ring;
			if (ring != null) {
				if (ring != lastRing) tooltip.add("");
				//tooltip.add("Final " + TextFormatting.BLUE + "Mana" + TextFormatting.GRAY + "/" + TextFormatting.RED + "Burnout" + TextFormatting.GRAY + " Cost: " + TextFormatting.BLUE + module.finalManaDrain + TextFormatting.GRAY + "/" + TextFormatting.RED + module.finalBurnoutFill);
				SpellRing tmpRing = ring;
				int i = 0;
				while (tmpRing != null) {
					tooltip.add(
							StringUtils.repeat("-", i) + "> "
									+ TextFormatting.GRAY
									+ tmpRing.getModuleReadableName()
									+ " - "
									+ TextFormatting.BLUE
									+ Math.round(tmpRing.getManaDrain() * tmpRing.getManaMultiplier())
									+ TextFormatting.GRAY
									+ "/"
									+ TextFormatting.RED
									+ Math.round(tmpRing.getBurnoutFill() * tmpRing.getBurnoutMultiplier()));
					if (GuiScreen.isShiftKeyDown()) {
						for (String key : tmpRing.getInformationTag().getKeySet()) {
							double value = tmpRing.getInformationTag().getDouble(key);
							String valueString;
							if (value < 10) valueString = String.format("%.2f", value);
							else if (value < 100) valueString = String.format("%.1f", value);
							else valueString = Double.toString(value);
							tooltip.add(StringUtils.repeat(" ", i + 1) + " | " + TextFormatting.DARK_GRAY + key + " x" + valueString);
						}
					}
					tmpRing = tmpRing.getChildRing();
					i++;
				}
			}
		}

		if (!GuiScreen.isShiftKeyDown() && !spellRings.isEmpty()) {
			TooltipHelper.addToTooltip(tooltip, "wizardry.misc.sneak_expanded");
		}

		if (spellRings.isEmpty() && ItemNBTHelper.getFloat(stack, Constants.NBT.PURITY_OVERRIDE, -1f) < 0) {
			float purity = getQuality(stack);
			String desc = super.getUnlocalizedName(stack) + ".";
			if (purity >= 1) desc += "perfect";
			else {
				boolean over = ItemNBTHelper.getInt(stack, Constants.NBT.PURITY, 0) > Constants.NBT.NACRE_PURITY_CONVERSION;
				if (purity >= 5 / 6.0)
					if (over)
						desc += "over_near";
					else
						desc += "under_near";
				else if (over)
					desc += "overdone";
				else
					desc += "underdone";

			}
			desc += ".desc";
			String used = LibrarianLib.PROXY.canTranslate(desc) ? desc : desc + "0";
			if (LibrarianLib.PROXY.canTranslate(used)) {
				TooltipHelper.addToTooltip(tooltip, used);
				int i = 0;
				while (LibrarianLib.PROXY.canTranslate(desc + (++i)))
					TooltipHelper.addToTooltip(tooltip, desc + i);
			}
		} else if (spellRings.isEmpty() && getQuality(stack) > 1f) {
			String desc = super.getUnlocalizedName(stack) + ".ancient.desc";
			String used = LibrarianLib.PROXY.canTranslate(desc) ? desc : desc + "0";
			if (LibrarianLib.PROXY.canTranslate(used)) {
				TooltipHelper.addToTooltip(tooltip, used);
				int i = 0;
				while (LibrarianLib.PROXY.canTranslate(desc + (++i)))
					TooltipHelper.addToTooltip(tooltip, desc + i);
			}
		}
	}

	@Override
	public void getSubItems(@Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab))
			subItems.add(new ItemStack(this));
	}

	private boolean requiresBowAction(ItemStack stack) {
		for (SpellRing spellRing : SpellUtils.getAllSpellRings(stack))
			if (spellRing.isContinuous() || spellRing.getChargeUpTime() > 0) {
				return true;
			}
		return false;
	}

	private boolean isContinuousSpell(ItemStack stack) {
		for (SpellRing spellRing : SpellUtils.getAllSpellRings(stack))
			if (spellRing.isContinuous()) {
				return true;
			}
		return false;
	}

	private int getChargeupTime(ItemStack stack) {
		int maxChargeUp = 0;
		for (SpellRing spellRing : SpellUtils.getAllSpellRings(stack)) {
			Set<SpellRing> overridingRings = spellRing.getOverridingRings();
			if (spellRing.isContinuous()) {
				return 72000;
			} else if (spellRing.getChargeUpTime() > maxChargeUp)
				maxChargeUp = spellRing.getChargeUpTime();

			for (SpellRing ring : overridingRings) {
				maxChargeUp = ring.getChargeUpTime();
			}
		}

		return maxChargeUp;
	}
}
