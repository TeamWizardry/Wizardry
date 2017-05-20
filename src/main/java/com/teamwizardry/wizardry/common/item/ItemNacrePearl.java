package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.api.item.IExplodable;
import com.teamwizardry.wizardry.api.item.IInfusable;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * Created by Saad on 6/28/2016.
 */
public class ItemNacrePearl extends ItemMod implements IInfusable, IExplodable, INacreColorable {

	public ItemNacrePearl() {
		super("nacre_pearl");
		setMaxStackSize(1);
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

	@Nonnull
	@Override
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		StringBuilder finalName = null;
		Set<Module> modules = SpellStack.getModules(stack);
		Module lastModule = null;
		for (Module module : modules) {
			if (module != null) {
				Module tempModule = module;
				while (tempModule != null) {

					if (finalName == null) {
						finalName = new StringBuilder(tempModule.getReadableName());
					} else {
						if (lastModule.getModuleType() == tempModule.getModuleType()) {
							finalName.append(" & ").append(tempModule.getReadableName());
						} else {
							finalName.append(" ").append(tempModule.getReadableName());
						}
					}

					if (tempModule.getModuleType() == ModuleType.SHAPE) finalName.append(" of");

					lastModule = tempModule;
					tempModule = tempModule.nextModule;
				}
			}
		}

		if (finalName == null)
			return ("" + I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name")).trim();
		else return finalName.toString();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		Set<Module> modules = SpellStack.getModules(stack);
		for (Module module : modules) {
			if (module != null) {
				tooltip.add("Final " + TextFormatting.BLUE + "Mana" + TextFormatting.GRAY + "/" + TextFormatting.RED + "Burnout" + TextFormatting.GRAY + " Cost: " + TextFormatting.BLUE + module.finalManaDrain + TextFormatting.GRAY + "/" + TextFormatting.RED + module.finalBurnoutFill);
				Module tempModule = module;
				int i = 0;
				while (tempModule != null) {
					tooltip.add(new String(new char[i]).replace("\0", "-") + "> " + TextFormatting.BLUE + tempModule.getManaDrain() + TextFormatting.GRAY + "/" + TextFormatting.RED + tempModule.getBurnoutFill() + TextFormatting.GRAY + " - " + tempModule.getReadableName());
					for (String key : tempModule.attributes.getKeySet())
						tooltip.add(new String(new char[i]).replace("\0", "-") + "^ " + TextFormatting.YELLOW + key + TextFormatting.GRAY + " * " + TextFormatting.GREEN + tempModule.attributes.getDouble(key));
					tempModule = tempModule.nextModule;
					i++;
				}
			}
		}
	}
}
