package com.teamwizardry.wizardry.common.tile;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
@TileRegister("magicians_worktable")
public class TileMagiciansWorktable extends TileMod {

	@Save
	public BlockPos linkedTable;

	public HashMap<SpellRing, UUID> paperComponents = new HashMap<>();
	@Deprecated
	public HashMultimap<Module, Module> modifiers = HashMultimap.create();
	public HashMap<UUID, UUID> componentLinks = new HashMap<>();

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound compound, boolean sync) {
		super.writeCustomNBT(compound, sync);

		NBTTagList list = new NBTTagList();
		for (Map.Entry<SpellRing, UUID> entrySet : paperComponents.entrySet()) {
			NBTTagCompound compound1 = new NBTTagCompound();
			compound1.setTag("ring", entrySet.getKey().serializeNBT());
			compound1.setString("uuid", entrySet.getValue().toString());
			list.appendTag(compound1);
		}
		compound.setTag("components", list);

		list = new NBTTagList();
		for (Map.Entry<UUID, UUID> entrySet : componentLinks.entrySet()) {
			NBTTagCompound compound1 = new NBTTagCompound();
			compound1.setString("uuid1", entrySet.getKey().toString());
			compound1.setString("uuid2", entrySet.getValue().toString());
			list.appendTag(compound1);
		}
		compound.setTag("links", list);
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound compound) {
		super.readCustomNBT(compound);
		componentLinks = new HashMap<>();
		paperComponents = new HashMap<>();

		for (NBTBase base : compound.getTagList("components", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound compound1 = (NBTTagCompound) base;
			if (compound1.hasKey("ring") && compound1.hasKey("uuid")) {

				NBTTagCompound nbtModule = compound.getCompoundTag("ring");

				paperComponents.put(SpellRing.deserializeRing(nbtModule), compound1.getUniqueId("uuid"));
			}
		}

		for (NBTBase base : compound.getTagList("links", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound compound1 = (NBTTagCompound) base;
			if (compound1.hasKey("uuid1") && compound1.hasKey("uuid2")) {
				componentLinks.put(UUID.fromString(compound1.getString("uuid1")), UUID.fromString(compound1.getString("uuid2")));
			}
		}
	}
}
