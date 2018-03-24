package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemModBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import com.teamwizardry.wizardry.common.advancement.IPickupAchievement;
import com.teamwizardry.wizardry.common.advancement.ModAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 6/12/2016.
 */
public class ItemBook extends ItemModBook implements IPickupAchievement {

	public static Book BOOK = new Book("book");

	public ItemBook() {
		super("book");
		setMaxStackSize(1);
	}

	@Override
	public Advancement getAdvancementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
		return ModAdvancements.BOOK;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	public IBookGui createGui(@Nonnull EntityPlayer player, @Nullable World world, @Nonnull ItemStack stack) {
		return new GuiBook(BOOK, stack);
	}

	@Nonnull
	@Override
	public Book getBook(@Nonnull EntityPlayer player, @Nullable World world, @Nonnull ItemStack stack) {
		return BOOK;
	}
}
