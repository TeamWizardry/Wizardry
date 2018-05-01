package com.teamwizardry.wizardry.utils;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.item.EnumPearlType;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

public class InfusedItemStackNBTData {
	private float rand;
	private NBTTagList spellList;
	private String pearlType; 
	private final String prefix;
	
	public InfusedItemStackNBTData(String prefix) {
		this.prefix = prefix;
	}
	
	public InfusedItemStackNBTData initByStack(ItemStack stack) {
		spellList = ItemNBTHelper.getList(stack, prefix + Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		pearlType = ItemNBTHelper.getString(stack, prefix + Constants.NBT.PEARL_TYPE, "");
		if( pearlType.isEmpty() )
			pearlType = null;
		rand = ItemNBTHelper.getFloat(stack, prefix + Constants.NBT.RAND, 0);
		return this;
	}
	
	public InfusedItemStackNBTData setRand(float rand) {
		this.rand = rand;
		return this;
	}
	
	public float getRand() {
		return this.rand;
	}
	
	public InfusedItemStackNBTData setSpellList(NBTTagList spellList) {
		this.spellList = spellList;
		return this;
	}

	public InfusedItemStackNBTData setSpellList(SpellBuilder builder) {
		NBTTagList list = new NBTTagList();
		for (SpellRing spellRing : builder.getSpell()) {
			list.appendTag(spellRing.serializeNBT());
		}
		this.spellList = list;
		return this;
	}
	
	public NBTTagList getSpellList() {
		return this.spellList;
	}
	
	public InfusedItemStackNBTData setPearlType(EnumPearlType pearlType) {
		this.pearlType = pearlType.toString();
		return this;
	}
	
	public String getPearlType() {
		return this.pearlType;
	}
	
	public boolean isComplete() {
		return spellList != null && pearlType != null;
	}
	
	public void assignToStack(ItemStack target) {
		if( !isComplete() )
			throw new IllegalStateException("Can't assign incomplete state!");
		
		ItemNBTHelper.setFloat(target, prefix + Constants.NBT.RAND, rand);
		ItemNBTHelper.setString(target, prefix + Constants.NBT.PEARL_TYPE, pearlType);
		ItemNBTHelper.setList(target, prefix + Constants.NBT.SPELL, spellList);
	}
}
