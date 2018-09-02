package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.structure.ComponentStructurePage;
import com.teamwizardry.wizardry.api.block.WizardryStructureRenderCompanion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ComponentWizardryStructure extends ComponentStructurePage {

	private final IBookGui book;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	@Nullable
	private WizardryStructureRenderCompanion structure;

	public ComponentWizardryStructure(@NotNull IBookGui book, int x, int y, int width, int height, @Nullable WizardryStructureRenderCompanion structure) {
		super(book, x, y, width, height, null);
		this.book = book;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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
			structure.draw(Minecraft.getMinecraft().world, 1f);
			GlStateManager.color(1f, 1f, 1f, 1f);
		}
	}
}
