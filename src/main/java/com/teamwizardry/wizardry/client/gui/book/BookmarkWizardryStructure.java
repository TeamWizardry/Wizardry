package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.ComponentBookMark;
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.ComponentMaterialsBar;
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.StructureMaterials;
import com.teamwizardry.wizardry.common.core.WizardryStructure;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.Template;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BookmarkWizardryStructure implements Bookmark {

	private ResourceLocation location;

	public BookmarkWizardryStructure(ResourceLocation location) {
		this.location = location;
	}

	@NotNull
	@Override
	public ComponentBookMark createBookmarkComponent(@NotNull IBookGui book, int bookmarkIndex) {
		WizardryStructure structure = ModStructures.structureManager.getStructure(location);

		HashMap<List<IBlockState>, Integer> map = new HashMap<>();
		if (structure != null)
			for (Template.BlockInfo info : structure.blockInfos()) {
				if (info.blockState.getBlock() == Blocks.AIR) continue;

				List<IBlockState> list = new ArrayList<>();
				list.add(info.blockState);
				map.put(list, map.getOrDefault(list, 0) + 1);
			}

		return new ComponentMaterialsBar(book, bookmarkIndex, new StructureMaterials(map));
	}
}
