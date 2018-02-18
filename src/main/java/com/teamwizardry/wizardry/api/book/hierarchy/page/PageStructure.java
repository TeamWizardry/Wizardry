package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.api.structure.StructureCacheRegistry;
import com.teamwizardry.wizardry.client.gui.book.ComponentStructure;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PageStructure implements Page {

	private String structureName;

	public PageStructure(JsonObject object) {
		structureName = object.getAsJsonPrimitive("value").getAsString();
	}

	@NotNull
	@Override
	public String getType() {
		return "structure";
	}

	@Override
	public @Nullable List<String> getSearchableStrings() {
		return Lists.newArrayList(structureName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiComponent> createBookComponents(GuiBook book, Vec2d size) {
		return Lists.newArrayList(
				new ComponentStructure(0, 0, size.getXi(), size.getYi(),
						StructureCacheRegistry.INSTANCE.getStructureOrAdd(structureName)));
	}
}
