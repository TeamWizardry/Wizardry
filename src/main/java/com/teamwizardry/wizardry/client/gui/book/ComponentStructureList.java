package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.structure.Structure;
import net.minecraft.block.Block;
import net.minecraft.world.gen.structure.template.Template;

import java.util.HashMap;

public class ComponentStructureList extends GuiComponent {

	public ComponentStructureList(Structure structure) {
		super(0, 0, 200, 300);
		HashMap<Block, Integer> ingredients = new HashMap<>();

		for (Template.BlockInfo info : structure.blockInfos()) {
			ingredients.putIfAbsent(info.blockState.getBlock(), 0);
			ingredients.put(info.blockState.getBlock(), ingredients.get(info.blockState.getBlock()) + 1);
		}

		StringBuilder builder = new StringBuilder();
		for (Block block : ingredients.keySet()) {
			builder.append(block.getLocalizedName()).append(" x").append(ingredients.get(block)).append("\n");
		}

		ComponentText recipeText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		recipeText.getWrap().setValue(50);
		recipeText.getTransform().setScale(2);
		recipeText.getText().setValue(builder.toString());

		add(recipeText);
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
