package com.teamwizardry.wizardry.client.gui.book;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentStack;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.PaginationContext;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ComponentSpellRecipe implements IBookElement {

	private final Book book;

	public ComponentSpellRecipe(Book book) {
		this.book = book;
	}

	@NotNull
	@Override
	public Book getBookParent() {
		return book;
	}

	@NotNull
	@Override
	public List<Bookmark> addAllBookmarks(@Nullable List<? extends Bookmark> list) {
		return DefaultImpls.addAllBookmarks(this, list);
	}

	@NotNull
	@Override
	public List<PaginationContext> createComponents(@NotNull IBookGui book) {
		return components((GuiBook) book);
	}

	public static List<PaginationContext> components(GuiBook book) {

		List<PaginationContext> contexts = Lists.newArrayList();

		if (book.getBookItemStack().isEmpty()) return contexts;
		ItemStack bookStack = book.getBookItemStack();

		if (!ItemNBTHelper.getBoolean(bookStack, "has_spell", false)) return contexts;

		NBTTagList moduleList = ItemNBTHelper.getList(bookStack, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
		if (moduleList == null) return contexts;

		List<List<Module>> spellModules = SpellUtils.deserializeModuleList(moduleList);
		List<ItemStack> spellItems = SpellUtils.getSpellItems(spellModules);
		spellModules = SpellUtils.getEssentialModules(spellModules);

		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

		int widthOfSpace = fr.getStringWidth(" ");
		StringBuilder builder = new StringBuilder(LibrarianLib.PROXY.translate("wizardry.book.spell_recipe_structure") + "\n");
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

				pageFromString(book, contexts, pageChunk);

				pageChunk = new StringBuilder();
			}
		}

		if (count != 0) pageFromString(book, contexts, pageChunk);

		Consumer<ComponentVoid> applier = component -> {};

		for (int i = 0; i < spellItems.size(); i++) {
			ItemStack stack = spellItems.get(i);

			int index = i;
			applier = applier.andThen(component -> {
				ComponentStack componentStack = new ComponentStack((index % 4) * 32, (index / 4) * 16);
				componentStack.getStack().setValue(stack);
				component.add(componentStack);

				if (index != spellItems.size() - 1 && (index % 4) < 3) {
					ComponentSprite nextItem = new ComponentSprite(book.getHomeSprite(), 32 + (index % 4) * 32, (index / 4) * 16 + 13, 16, 8);
					nextItem.getColor().setValue(book.getBook().getHighlightColor());
					nextItem.getTransform().setRotate(Math.toRadians(180));
					component.add(nextItem);
				}
			});

			if ((index / 4) >= 9) {
				Consumer<ComponentVoid> spellApplier = applier;
				contexts.add(new PaginationContext(() -> {
					ComponentVoid component = new ComponentVoid(16, 16,
							book.getMainBookComponent().getSize().getXi() - 32,
							book.getMainBookComponent().getSize().getYi() - 32);
					spellApplier.accept(component);
					return component;
				}));

				applier = component -> {};
			}
		}

		Consumer<ComponentVoid> spellApplier = applier;
		contexts.add(new PaginationContext(() -> {
			ComponentVoid component = new ComponentVoid(16, 16,
					book.getMainBookComponent().getSize().getXi() - 32,
					book.getMainBookComponent().getSize().getYi() - 32);
			spellApplier.accept(component);
			return component;
		}));

		return contexts;
	}

	private static void pageFromString(GuiBook book, List<PaginationContext> contexts, StringBuilder page) {
		contexts.add(new PaginationContext(() -> {
			ComponentText spellStructureText = new ComponentText(16, 16, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			spellStructureText.getUnicode().setValue(true);
			spellStructureText.getEnableUnicodeBidi().setValue(false);
			spellStructureText.getText().setValue(page.toString());
			spellStructureText.getWrap().setValue(book.getMainBookComponent().getSize().getXi() - 32);
			return spellStructureText;
		}));
	}
}
