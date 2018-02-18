package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Page {

	@NotNull
	String getType();

	@Nullable
	default List<String> getSearchableStrings() {
		return null;
	}

	@Nullable
	default List<String> getSearchableKeys() {
		return null;
	}

	@SideOnly(Side.CLIENT)
	List<GuiComponent> createBookComponents(GuiBook book, Vec2d size);
}
