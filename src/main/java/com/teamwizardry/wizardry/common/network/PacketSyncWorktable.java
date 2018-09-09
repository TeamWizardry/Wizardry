package com.teamwizardry.wizardry.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.api.spell.CommonWorktableModule;
import com.teamwizardry.wizardry.common.tile.TileMagiciansWorktable;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
@PacketRegister(Side.SERVER)
public class PacketSyncWorktable extends PacketBase {

	@Save
	public NBTTagList commonModules;
	@Save
	private int world;
	@Save
	private BlockPos pos;

	public PacketSyncWorktable() {
	}

	public PacketSyncWorktable(int world, BlockPos pos, Set<CommonWorktableModule> commonModules) {
		this.world = world;
		this.pos = pos;
		if (commonModules == null) return;

		NBTTagList compiledList = new NBTTagList();
		for (CommonWorktableModule commonModule : commonModules) {
			compiledList.appendTag(commonModule.serializeNBT());
		}
		this.commonModules = compiledList;
	}

	@Override
	public void handle(@Nonnull MessageContext messageContext) {
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.world);

		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof TileMagiciansWorktable) {
			((TileMagiciansWorktable) entity).setCommonModules(commonModules);

			BlockPos sister = ((TileMagiciansWorktable) entity).linkedTable;
			TileEntity sisterTile = world.getTileEntity(sister);
			if (sisterTile instanceof TileMagiciansWorktable) {
				((TileMagiciansWorktable) sisterTile).setCommonModules(commonModules);
			}
		}
	}
}
