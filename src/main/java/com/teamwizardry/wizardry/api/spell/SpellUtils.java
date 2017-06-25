package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class SpellUtils {

	public static void runSpell(@Nonnull ItemStack spellHolder, @Nonnull SpellData data) {
		if (data.world.isRemote) return;

		if (spellHolder.getItem() instanceof INacreColorable) {
			float purity = ((INacreColorable) spellHolder.getItem()).getQuality(spellHolder);
			double multiplier;
			if (purity >= 1f) multiplier = ConfigValues.perfectPearlMultiplier * purity;
			else if (purity <= 0f) multiplier = ConfigValues.damagedPearlMultiplier;
			else {
				double base = purity - 1;
				multiplier = 1 - (base * base * base * base);
			}

			for (Module module : SpellUtils.getAllModules(spellHolder))
				module.setMultiplier(module.getMultiplier() * multiplier);

		}

		for (Module module : getModules(spellHolder)) {
			module.castSpell(data);
		}
	}

	public static ArrayList<ArrayList<Module>> getModules(@Nonnull ArrayList<Module> moduleHeads) {
		ArrayList<ArrayList<Module>> modules = new ArrayList<>();

		for (Module module : moduleHeads) modules.add(getAllModules(module));

		return modules;
	}

	public static ArrayList<Module> getModules(@Nonnull ItemStack spellHolder) {
		ArrayList<Module> modules = new ArrayList<>();

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (list == null) return modules;

		return getModules(list);
	}

	public static ArrayList<Module> getModules(@Nonnull NBTTagCompound compound) {
		if (compound.hasKey(Constants.NBT.SPELL))
			return getModules(compound.getTagList(Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND));
		else return new ArrayList<>();
	}

	public static ArrayList<Module> getModules(@Nonnull NBTTagList list) {
		ArrayList<Module> modules = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module == null) continue;
			module = module.copy();
			module.deserializeNBT(compound);
			modules.add(module);
		}
		return modules;
	}

	public static ArrayList<Module> getAllModules(@Nonnull NBTTagCompound compound) {
		ArrayList<Module> modules = new ArrayList<>();
		ArrayList<Module> heads = getModules(compound);
		for (Module module : heads) {
			Module tempModule = module;
			while (tempModule != null) {
				modules.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules;
	}

	public static ArrayList<Module> getAllModules(@Nonnull Module module) {
		ArrayList<Module> modules = new ArrayList<>();
		Module tempModule = module;
		while (tempModule != null) {
			modules.add(tempModule);
			tempModule = tempModule.nextModule;
		}
		return modules;
	}

	public static ArrayList<Module> getAllModules(@Nonnull ArrayList<Module> modules) {
		ArrayList<Module> modules1 = new ArrayList<>();
		for (Module module : modules) {
			Module tempModule = module;
			while (tempModule != null) {
				modules1.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules1;
	}

	public static ArrayList<Module> getAllModules(@Nonnull ItemStack spellHolder) {
		ArrayList<Module> modules = new ArrayList<>();
		ArrayList<Module> heads = getModules(spellHolder);
		for (Module module : heads) {
			Module tempModule = module;
			while (tempModule != null) {
				modules.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules;
	}
}
