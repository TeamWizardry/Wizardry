package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.ConfigValues;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.item.INacreColorable;
import com.teamwizardry.wizardry.api.spell.module.SpellRing;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class SpellUtils {

	public static void runSpell(@Nonnull ItemStack spellHolder, @Nonnull SpellData data) {
		if (data.world.isRemote) return;

		Entity caster = data.getData(SpellData.DefaultKeys.CASTER);
		if (caster != null && caster instanceof EntityLivingBase && BaublesSupport.getItem((EntityLivingBase) caster, ModItems.CREATIVE_HALO, ModItems.FAKE_HALO, ModItems.REAL_HALO).isEmpty())
			return;

		if (spellHolder.getItem() instanceof INacreColorable) {
			float purity = ((INacreColorable) spellHolder.getItem()).getQuality(spellHolder);
			double multiplier;
			if (purity >= 1f) multiplier = ConfigValues.perfectPearlMultiplier * purity;
			else if (purity <= ConfigValues.damagedPearlMultiplier) multiplier = ConfigValues.damagedPearlMultiplier;
			else {
				double base = purity - 1;
				multiplier = 1 - (base * base * base * base);
			}

			for (SpellRing module : SpellUtils.getAllSpellRings(spellHolder))
				module.setMultiplier(module.getMultiplier() * multiplier);

		}

		for (SpellRing module : getSpellRings(spellHolder)) {
			module.castSpell(data, this);
		}
	}

	public static ArrayList<ArrayList<SpellRing>> getSpellRings(@Nonnull List<SpellRing> moduleHeads) {
		ArrayList<ArrayList<SpellRing>> rings = new ArrayList<>();

		for (SpellRing module : moduleHeads) rings.add(getAllSpellRings(module));

		return rings;
	}

	public static ArrayList<SpellRing> getSpellRings(@Nonnull ItemStack spellHolder) {
		ArrayList<SpellRing> rings = new ArrayList<>();

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (list == null) return rings;

		return getSpellRings(list);
	}

	public static ArrayList<SpellRing> getSpellRings(@Nonnull NBTTagCompound compound) {
		if (compound.hasKey(Constants.NBT.SPELL))
			return getSpellRings(compound.getTagList(Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND));
		else return new ArrayList<>();
	}

	public static ArrayList<SpellRing> getSpellRings(@Nonnull NBTTagList list) {
		ArrayList<SpellRing> rings = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			SpellRing ring = SpellRing.deserializeRing(compound);
			if (ring == null) continue;
			rings.add(ring);
		}
		return rings;
	}

	public static ArrayList<SpellRing> getAllSpellRings(@Nonnull NBTTagCompound compound) {
		ArrayList<SpellRing> rings = new ArrayList<>();
		ArrayList<SpellRing> heads = getSpellRings(compound);
		for (SpellRing module : heads) {
			SpellRing tempSpellRing = module;
			while (tempSpellRing != null) {
				rings.add(tempSpellRing);
				tempSpellRing = tempSpellRing.getChildRing();
			}
		}
		return rings;
	}

	public static ArrayList<SpellRing> getAllSpellRings(@Nonnull SpellRing module) {
		ArrayList<SpellRing> rings = new ArrayList<>();
		SpellRing tempSpellRing = module;
		while (tempSpellRing != null) {
			rings.add(tempSpellRing);
			tempSpellRing = tempSpellRing.getChildRing();
		}
		return rings;
	}

	public static ArrayList<SpellRing> getAllSpellRings(@Nonnull ArrayList<SpellRing> rings) {
		ArrayList<SpellRing> rings1 = new ArrayList<>();
		for (SpellRing module : rings) {
			SpellRing tempSpellRing = module;
			while (tempSpellRing != null) {
				rings1.add(tempSpellRing);
				tempSpellRing = tempSpellRing.getChildRing();
			}
		}
		return rings1;
	}

	public static ArrayList<SpellRing> getAllSpellRings(@Nonnull ItemStack spellHolder) {
		ArrayList<SpellRing> rings = new ArrayList<>();
		ArrayList<SpellRing> heads = getSpellRings(spellHolder);
		for (SpellRing module : heads) {
			SpellRing tempSpellRing = module;
			while (tempSpellRing != null) {
				rings.add(tempSpellRing);
				tempSpellRing = tempSpellRing.getChildRing();
			}
		}
		return rings;
	}
}
