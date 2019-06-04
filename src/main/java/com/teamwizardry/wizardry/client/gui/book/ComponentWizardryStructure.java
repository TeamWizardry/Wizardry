package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.ComponentStructurePage;
import com.teamwizardry.wizardry.init.ModStructures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentWizardryStructure extends ComponentStructurePage {

	private final IBookGui book;
	private @Nullable ResourceLocation structure;

	public ComponentWizardryStructure(@NotNull IBookGui book, int x, int y, int width, int height, @Nullable ResourceLocation structure) {
		super(book, x, y, width, height, null);
		this.book = book;
		this.structure = structure;
	}

	@NotNull
	@Override
	public ComponentStructurePage copy() {
		ComponentWizardryStructure str = new ComponentWizardryStructure(book, getPos().getXi(), getPos().getYi(), getSize().getYi(), getSize().getYi(), structure);
		str.setRotVec(getRotVec());
		str.setPanVec(getPanVec());
		str.setPrevPos(getPrevPos());
		return str;
	}

	@Override
	public boolean failed() {
		return false;
	}

	@Override
	public void preShift() {

	}

	@Override
	public void render(int time) {
		if (structure != null) {
			//GlStateManager.translate(-structure.perfectCenter.x - 0.5, -structure.perfectCenter.y - 0.5, -structure.perfectCenter.z - 0.5);
			GlStateManager.color(1f, 1f, 1f);
			ModStructures.structureManager.draw(structure, 1f);
			GlStateManager.color(1f, 1f, 1f, 1f);
		}
	}
}
