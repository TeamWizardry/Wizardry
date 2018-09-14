package com.teamwizardry.wizardry.common.tile;

import com.teamwizardry.librarianlib.features.autoregister.TileRegister;
import com.teamwizardry.librarianlib.features.base.block.tile.TileMod;
import com.teamwizardry.librarianlib.features.saving.Save;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.CommonWorktableModule;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
@TileRegister(Wizardry.MODID + ":magicians_worktable")
public class TileMagiciansWorktable extends TileMod {

	@Save
	public BlockPos linkedTable;
	@Save
	public NBTTagList commonModules;

	public void setCommonModules(@Nonnull Set<CommonWorktableModule> commonModules) {
		NBTTagList commonList = new NBTTagList();
		for (CommonWorktableModule commonModule : commonModules) {
			commonList.appendTag(commonModule.serializeNBT());
		}
		this.commonModules = commonList;
		markDirty();
	}

	public Set<CommonWorktableModule> getCommonModules() {
		Set<CommonWorktableModule> commonModules = new HashSet<>();

		if (this.commonModules != null) {
			for (NBTBase base : this.commonModules) {
				if (base instanceof NBTTagCompound) {
					NBTTagCompound compound = (NBTTagCompound) base;

					CommonWorktableModule commonModule = CommonWorktableModule.deserailize(compound);
					commonModules.add(commonModule);
				}
			}
		}

		return commonModules;
	}

	public void setCommonModules(NBTTagList commonModules) {
		this.commonModules = commonModules;
		markDirty();
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}
}
