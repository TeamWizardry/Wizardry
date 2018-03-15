package com.teamwizardry.wizardry.client.gui.book;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
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
					margin = StringUtils.repeat(" ", nbOfSpace) + "|_";

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

				getPages().add(spellStructureText);
			}
		}

		if (!pageChunk.toString().isEmpty()) {
			ComponentText spellStructureText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			spellStructureText.getText().setValue(pageChunk.toString());
			spellStructureText.getUnicode().setValue(true);
			spellStructureText.getWrap().setValue(getSize().getXi());

			getPages().add(spellStructureText);
		}

		SpellBuilder spellBuilder = new SpellBuilder(spellChains);

		ComponentVoid page = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());

		int row = 0;
		List<ItemStack> inventory = spellBuilder.getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.get(i);

			if (i != 0 && i % 4 == 0) row++;

			ComponentStack componentStack = new ComponentStack((i % 4) * 32, row * 16);
			componentStack.getStack().setValue(stack);
			page.add(componentStack);

			if (row >= 20) {
				row = 0;
				getPages().add(page);
				page = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());
			}
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
