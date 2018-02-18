package com.teamwizardry.wizardry.api.book.hierarchy.page;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.wizardry.client.gui.book.ComponentRecipe;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PageRecipe implements Page {

	private ResourceLocation recipe;

	public PageRecipe(JsonObject jsonElement) {
		recipe = new ResourceLocation(jsonElement.getAsJsonPrimitive("value").getAsString());
	}

	@NotNull
	@Override
	public String getType() {
		return "recipe";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<GuiComponent> createBookComponents(GuiBook book, Vec2d size) {
		return Lists.newArrayList(new ComponentRecipe(0, 0, size.getXi(), size.getYi(), book.mainColor, recipe));
	}
}
