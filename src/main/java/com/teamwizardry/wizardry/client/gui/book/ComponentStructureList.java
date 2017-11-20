package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.structure.Structure;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class ComponentStructureList extends GuiComponent {

	public ComponentStructureList(Structure structure) {
		super(0, 0, 200, 300);
		Deque<Block> sorted = new ArrayDeque<>();
		HashMap<Block, Integer> ingredients = new HashMap<>();

		for (Template.BlockInfo info : structure.blockInfos()) {
			if (info.blockState.getBlock() == Blocks.AIR) continue;

			ingredients.putIfAbsent(info.blockState.getBlock(), 0);
			ingredients.put(info.blockState.getBlock(), ingredients.get(info.blockState.getBlock()) + 1);

			if (!sorted.contains(info.blockState.getBlock())) sorted.add(info.blockState.getBlock());
		}


		ComponentVoid recipe = new ComponentVoid(0, 0, 200, 300);
		recipe.getTransform().setScale(2);
		add(recipe);

		ComponentText text = new ComponentText(0, 3, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		text.setSize(new Vec2d(200, 16));
		text.getText().setValue(" Materials Required");
		recipe.add(text);

		ComponentSprite lineBreak = new ComponentSprite(BookGui.LINE_BREAK, (int) (getSize().getX() / 2.0 - 177.0 / 2.0), 30, 177, 2);
		add(lineBreak);

		int i = 0;
		int row = 0;
		while (!sorted.isEmpty() && sorted.peek() != null) {

			Block nextBlock = sorted.pop();
			ItemStack stack;
			if (FluidRegistry.lookupFluidForBlock(nextBlock) != null) {
				stack = FluidUtil.getFilledBucket(new FluidStack(FluidRegistry.lookupFluidForBlock(nextBlock), 1));
				stack = new ItemStack(stack.getItem(), ingredients.get(nextBlock), stack.getMetadata(), stack.getTagCompound());
			} else {
				stack = new ItemStack(nextBlock, ingredients.get(nextBlock));
			}

			ComponentStack componentStack = new ComponentStack(i * 16, 20 + row * 16);
			componentStack.getStack().setValue(stack);
			recipe.add(componentStack);

			if (i++ > 4) {
				i = 0;
				row++;
			}
		}
	}

	@Override
	public void drawComponent(Vec2d mousePos, float partialTicks) {

	}
}
