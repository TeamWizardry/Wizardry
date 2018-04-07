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
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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

		NBTTagList moduleList = ItemNBTHelper.getList(bookStack, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
		if (moduleList == null) return;

		List<List<Module>> spellModules = SpellUtils.deserializeModuleList(moduleList);
		List<ItemStack> spellItems = SpellUtils.getSpellItems(spellModules);
		spellModules = SpellUtils.getEssentialModules(spellModules);

		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

		int widthOfSpace = fr.getStringWidth(" ");
		StringBuilder builder = new StringBuilder("LOCALIZE ME DAMMIT (originally \"Spell Structure:\")\n");
		for (List<Module> spellModuleList : spellModules) {
			String margin = null;
			for (Module module : spellModuleList) {
				if (margin == null) {
					margin = " - ";
					builder.append(margin).append(module.getReadableName()).append("\n");
				} else {
					int realLength = fr.getStringWidth(margin);
					int nbOfSpace = MathHelper.clamp(realLength / widthOfSpace, 0, 17);
					margin = StringUtils.repeat(" ", nbOfSpace) + "|_ ";
					builder.append(margin).append(module.getReadableName()).append("\n");

					if (nbOfSpace >= 16) {
						builder.append("   ________________|").append("\n");
						margin = "   ";
					}
				}
			}
		}

		String[] lines = builder.toString().split("\n");
		StringBuilder pageChunk = new StringBuilder();
		int count = 0;
		for (String line : lines) {
			pageChunk.append(line).append("\n");

			if (++count >= 16) {
				count = 0;

				ComponentText spellStructureText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
				spellStructureText.getUnicode().setValue(true);
				spellStructureText.getText().setValue(pageChunk.toString());
				spellStructureText.getWrap().setValue(getSize().getXi());

				addPage(spellStructureText);

				pageChunk = new StringBuilder();
			}
		}

		if (count != 0) {
			ComponentText spellStructureText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			spellStructureText.getUnicode().setValue(true);
			spellStructureText.getText().setValue(pageChunk.toString());
			spellStructureText.getWrap().setValue(getSize().getXi());

			addPage(spellStructureText);
		}

		ComponentVoid page = new ComponentVoid(0, 0, getSize().getXi(), getSize().getYi());

		int row = 0;
		int column = 0;
		for (int i = 0; i < spellItems.size(); i++) {
			ItemStack stack = spellItems.get(i);

			ComponentStack componentStack = new ComponentStack(column * 32, row * 16);
			componentStack.getStack().setValue(stack);
			page.add(componentStack);

			if (i != spellItems.size() - 1 && column < 3) {
				ComponentSprite nextItem = new ComponentSprite(book.getHomeSprite(), 32 + column * 32, row * 16 + 13, 16, 8);
				nextItem.getColor().setValue(book.getBook().getHighlightColor());
				nextItem.getTransform().setRotate(Math.toRadians(180));
				page.add(nextItem);
			}


			if (++column >= 4) {
				column = 0;
				row++;
			}

			if (row >= 9) {
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
