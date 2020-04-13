package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.wizardry.api.NBTConstants;
import com.teamwizardry.wizardry.api.capability.player.mana.CustomManaCapability;
import com.teamwizardry.wizardry.api.capability.player.mana.ManaCapabilityProvider;
import com.teamwizardry.wizardry.api.item.INacreProduct;
import com.teamwizardry.wizardry.api.item.IPotionEffectExplodable;
import com.teamwizardry.wizardry.api.item.ISpellInfusable;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Demoniaque on 6/28/2016.
 */
public class NacrePearlSpell extends ItemMod implements ISpellInfusable, IPotionEffectExplodable, INacreProduct {

	public NacrePearlSpell() {
		super("nacre_pearl");
		setMaxStackSize(1);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new ManaCapabilityProvider(new CustomManaCapability(300, 300, 0, 0));
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

	private String getNameType(@Nonnull ItemStack stack) {
		float quality = this.getQuality(stack);
		if (quality > 1)
			return "ancient";
		else if (quality == 1)
			return "apex";
		else if (quality > 0.8)
			return "potent";
		else if (quality > 0.6)
			return "decent";
		else if (quality > 0.4)
			return "flawed";
		else if (quality > 0.2)
			return "drained";
		return "wasted";
	}

	@NotNull
	@Override
	public String getTranslationKey(@NotNull ItemStack stack) {
		if (!stack.hasTagCompound())
			return super.getTranslationKey(stack);
		return super.getTranslationKey(stack) + "." + getNameType(stack);
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
									+ Math.round(tmpRing.getManaDrain(null) * tmpRing.getManaMultiplier())
									+ TextFormatting.GRAY
									+ "/"
									+ TextFormatting.RED
									+ Math.round(tmpRing.getBurnoutFill(null) * tmpRing.getBurnoutMultiplier()));
					if (GuiScreen.isShiftKeyDown()) {
						for (String key : tmpRing.getInformationTag().getKeySet())
						{
							int value = (int) tmpRing.getInformationTag().getDouble(key);
							tooltip.add(StringUtils.repeat(" ", i + 1) + " | " + TextFormatting.DARK_GRAY + key + " x" + value);
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

		if (spellRings.isEmpty() && NBTHelper.getFloat(stack, NBTConstants.NBT.PURITY_OVERRIDE, -1f) < 0) {
			float purity = getQuality(stack);
			String desc = super.getTranslationKey(stack) + ".";
			if (purity >= 1) desc += "perfect";
			else {
				boolean over = NBTHelper.getInt(stack, NBTConstants.NBT.PURITY, 0) > NBTConstants.NBT.NACRE_PURITY_CONVERSION;
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
			String desc = super.getTranslationKey(stack) + ".ancient.desc";
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
		if (isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this));
			ItemStack stack = new ItemStack(this);
			NBTHelper.setFloat(stack, NBTConstants.NBT.PURITY_OVERRIDE, 2f);
			subItems.add(stack);
		}
	}

	@NotNull
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		return EnumRarity.UNCOMMON;
	}
}
