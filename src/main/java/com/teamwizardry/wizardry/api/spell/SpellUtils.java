package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
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

		for (SpellRing spellRing : getSpellChains(spellHolder)) {
			spellRing.runSpellRing(data);
		}
	}

	/**
	 * Gets all SpellRings that exist in an ItemStack with the children of each ring inside
	 * of them, compressed essentially.
	 * This basically returns the head of each spell chain only.
	 *
	 * @param spellHolder The ItemStack containing the spells.
	 * @return List with all spell ring heads in the stack.
	 */
	public static List<SpellRing> getSpellChains(@Nonnull ItemStack spellHolder) {
		List<SpellRing> rings = new ArrayList<>();

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (list == null) return rings;

		return getSpellChains(list);
	}

	/**
	 * Gets all spell ring heads (containing children inside of each).
	 *
	 * @param list NBTTagList where each tag contains a whole SpellRing chain.
	 * @return List with the spell ring heads.
	 */
	public static List<SpellRing> getSpellChains(@Nonnull NBTTagList list) {
		ArrayList<SpellRing> rings = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			SpellRing ring = SpellRing.deserializeRing(compound);
			if (ring == null) continue;
			rings.add(ring);
		}
		return rings;
	}

	/**
	 * Gets all SpellRings children from the passed spellRing object with itself included.
	 *
	 * @param spellRing The SpellRing to uncompress.
	 * @return List with all spell rings that exist in the spellRing including itself.
	 */
	public static List<SpellRing> getAllSpellRings(@Nonnull SpellRing spellRing) {
		List<SpellRing> rings = new ArrayList<>();
		SpellRing tempSpellRing = spellRing;
		while (tempSpellRing != null) {
			rings.add(tempSpellRing);
			tempSpellRing = tempSpellRing.getChildRing();
		}
		return rings;
	}

	/**
	 * Gets all SpellRings that exist in an ItemStack with children of each ring included
	 * in the list returned.
	 *
	 * @param spellHolder The ItemStack containing the spell.
	 * @return List with all spell rings that exist in the stack.
	 */
	public static List<SpellRing> getAllSpellRings(@Nonnull ItemStack spellHolder) {
		List<SpellRing> rings = new ArrayList<>();
		List<SpellRing> heads = getSpellChains(spellHolder);
		for (SpellRing spellRing : heads) {
			SpellRing tempSpellRing = spellRing;
			while (tempSpellRing != null) {
				rings.add(tempSpellRing);
				tempSpellRing = tempSpellRing.getChildRing();
			}
		}
		return rings;
	}
}
