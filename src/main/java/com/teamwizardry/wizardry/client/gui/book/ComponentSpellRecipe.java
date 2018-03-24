package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.provided.book.EventNavBarChange;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.NavBarHolder;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.IBookElement;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellBuilder;
import com.teamwizardry.wizardry.api.spell.SpellRing;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class ComponentSpellRecipe extends NavBarHolder implements IBookElement {

	private GuiBook book;

	public ComponentSpellRecipe(GuiBook book) {
		super(16, 16, book.getMainBookComponent().getSize().getXi() - 32, book.getMainBookComponent().getSize().getYi() - 32, book);
		this.book = book;

		getNavBar().BUS.hook(EventNavBarChange.class, event -> {

		});

		if (book.getBookItemStack().isEmpty()) return;
		ItemStack bookStack = book.getBookItemStack();

		if (!ItemNBTHelper.getBoolean(bookStack, "has_spell", false)) return;

		NBTTagList spellList = ItemNBTHelper.getList(bookStack, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (spellList == null) return;

		List<SpellRing> spellChains = SpellUtils.getSpellChains(spellList);

		int widthOfSpace = Minecraft.getMinecraft().fontRenderer.getStringWidth(" ");
		StringBuilder builder = new StringBuilder("Spell Structure:\n");
		for (SpellRing chainHead : spellChains) {
			String margin = null;
			List<SpellRing> allSpellRings = SpellUtils.getAllSpellRings(chainHead);
			for (SpellRing ring : allSpellRings) {
				if (margin == null) {
					margin = " - ";

					builder.append(margin).append(ring.getModuleReadableName()).append("\n");
				} else {
					int realLength = Minecraft.getMinecraft().fontRenderer.getStringWidth(margin);
					int nbOfSpace = MathHelper.clamp(realLength / widthOfSpace, 0, 20);
					margin = StringUtils.repeat(" ", nbOfSpace) + "|_ ";

					builder.append(margin).append(ring.getModuleReadableName()).append("\n");
				}
			}
		}

		String[] lines = builder.toString().split("\n");
		StringBuilder pageChunk = new StringBuilder();
		int count = 0;
		for (String line : lines) {
			pageChunk.append(line).append("\n");

			if (++count >= 20) {
				count = 0;
				pageChunk = new StringBuilder();

				ComponentText spellStructureText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
				spellStructureText.getText().setValue(pageChunk.toString());
				spellStructureText.getUnicode().setValue(true);
				spellStructureText.getWrap().setValue(getSize().getXi());

				addPage(spellStructureText);
			}
		}

		if (count != 0) {
			ComponentText spellStructureText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			spellStructureText.getText().setValue(pageChunk.toString());
			spellStructureText.getUnicode().setValue(true);
			spellStructureText.getWrap().setValue(getSize().getXi());

			addPage(spellStructureText);
		}

		SpellBuilder spellBuilder = new SpellBuilder(spellChains, true, true);

		ComponentVoid page = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());

		int row = 0;
		int column = 0;
		List<ItemStack> inventory = spellBuilder.getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.get(i);

			ComponentStack componentStack = new ComponentStack(column * 32, row * 16);
			componentStack.getStack().setValue(stack);
			page.add(componentStack);

			if (i != inventory.size() - 1 && column < 3) {
				ComponentSprite nextItem = new ComponentSprite(book.getHomeSprite(), 32 + column * 32, row * 16 + 13, 16, 8);
				nextItem.getColor().setValue(book.getBook().getHighlightColor());
				nextItem.getTransform().setRotate(Math.toRadians(180));
				page.add(nextItem);
			}


			if (++column >= 4) {
				column = 0;
				row++;
			}

			if (row >= 20) {
				row = 0;
				addPage(page);
				page = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
			}
		}

		if (row != 0) {
			addPage(page);
		}
	}

	@Nonnull
	@Override
	public Book getBookParent() {
		return book.getBook();
	}

	@Nonnull
	@Override
	public GuiComponent createComponent(@Nonnull IBookGui book) {
		return new ComponentSpellRecipe(this.book);
	}

	@Nonnull
	@Override
	public IBookElement getHeldElement() {
		return this;
	}
}
