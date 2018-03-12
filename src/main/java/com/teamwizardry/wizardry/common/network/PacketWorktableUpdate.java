package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.common.tile.TileMagiciansWorktable;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Demoniaque.
 */
@PacketRegister(Side.SERVER)
public class PacketWorktableUpdate extends PacketBase {

	@Save
	private int worldID;
	@Save
	private BlockPos pos;

	private HashMap<SpellRing, UUID> components;
	private HashMap<UUID, UUID> links;

	public PacketWorktableUpdate() {
	}

	public PacketWorktableUpdate(int worldID, BlockPos pos, HashMap<SpellRing, UUID> components, HashMap<UUID, UUID> links) {
		this.worldID = worldID;
		this.pos = pos;
		this.components = components;
		this.links = links;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveGetter(NBTTagCompound compound) {
		if (compound == null) return;

		links = new HashMap<>();
		components = new HashMap<>();

		for (NBTBase base : compound.getTagList("components", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound compound1 = (NBTTagCompound) base;
			if (compound1.hasKey("ring") && compound1.hasKey("uuid")) {

				NBTTagCompound nbtModule = compound.getCompoundTag("ring");

				components.put(SpellRing.deserializeRing(nbtModule), compound1.getUniqueId("uuid"));
			}
		}

		for (NBTBase base : compound.getTagList("links", Constants.NBT.TAG_COMPOUND)) {
			NBTTagCompound compound1 = (NBTTagCompound) base;
			if (compound1.hasKey("uuid1") && compound1.hasKey("uuid2")) {
				links.put(UUID.fromString(compound1.getString("uuid1")), UUID.fromString(compound1.getString("uuid2")));
			}
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveSetter() {
		NBTTagCompound compound = new NBTTagCompound();

		if (components == null || links == null) return compound;

		NBTTagList list = new NBTTagList();
		for (Map.Entry<SpellRing, UUID> entrySet : components.entrySet()) {
			NBTTagCompound compound1 = new NBTTagCompound();
			compound1.setTag("ring", entrySet.getKey().serializeNBT());
			compound1.setString("uuid", entrySet.getValue().toString());
			list.appendTag(compound1);
		}
		compound.setTag("components", list);

		list = new NBTTagList();
		for (Map.Entry<UUID, UUID> entrySet : links.entrySet()) {
			NBTTagCompound compound1 = new NBTTagCompound();
			compound1.setString("uuid1", entrySet.getKey().toString());
			compound1.setString("uuid2", entrySet.getValue().toString());
			list.appendTag(compound1);
		}
		compound.setTag("links", list);

		return compound;
	}


	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(worldID);

		if (!world.isBlockLoaded(pos)) return;
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() != ModBlocks.MAGICIANS_WORKTABLE) return;

		TileEntity table = world.getTileEntity(pos);
		if (table == null) return;
		if (!(table instanceof TileMagiciansWorktable)) return;

		((TileMagiciansWorktable) table).componentLinks = links;
		((TileMagiciansWorktable) table).paperComponents = components;

		table.markDirty();
	}
}
