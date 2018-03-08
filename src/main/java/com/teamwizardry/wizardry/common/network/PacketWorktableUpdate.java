package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.api.spell.module.ModuleRegistry;
import com.teamwizardry.wizardry.common.tile.TileMagiciansWorktable;
import com.teamwizardry.wizardry.init.ModBlocks;
import net.minecraft.block.state.IBlockState;
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

	private HashMap<Module, UUID> components;
	private HashMap<UUID, UUID> links;

	public PacketWorktableUpdate() {
	}

	public PacketWorktableUpdate(int worldID, BlockPos pos, HashMap<Module, UUID> components, HashMap<UUID, UUID> links) {
		this.worldID = worldID;
		this.pos = pos;
		this.components = components;
		this.links = links;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		if (compound == null) return;

		components = new HashMap<>();
		links = new HashMap<>();

		NBTTagList list = compound.getTagList("components", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound1 = list.getCompoundTagAt(i);
			if (compound1.hasKey("module") && compound1.hasKey("uuid")) {

				NBTTagCompound nbtModule = compound1.getCompoundTag("module");

				if (nbtModule.hasKey("id")) {
					Module module = ModuleRegistry.INSTANCE.getModule(nbtModule.getString("id"));
					module.deserializeNBT(nbtModule);
					components.put(module, UUID.fromString(compound1.getString("uuid")));
				}
			}
		}

		list = compound.getTagList("links", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound1 = list.getCompoundTagAt(i);
			if (compound1.hasKey("uuid1") && compound1.hasKey("uuid2")) {
				links.put(UUID.fromString(compound1.getString("uuid1")), UUID.fromString(compound1.getString("uuid2")));
			}
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (components == null || links == null) return nbt;

		NBTTagList list = new NBTTagList();
		for (Map.Entry<Module, UUID> entrySet : components.entrySet()) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setTag("module", entrySet.getKey().serializeNBT());
			compound.setString("uuid", entrySet.getValue().toString());
			list.appendTag(compound);
		}
		nbt.setTag("components", list);

		list = new NBTTagList();
		for (Map.Entry<UUID, UUID> entrySet : links.entrySet()) {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setString("uuid1", entrySet.getKey().toString());
			compound.setString("uuid2", entrySet.getValue().toString());
			list.appendTag(compound);
		}
		nbt.setTag("links", list);
		return nbt;
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
