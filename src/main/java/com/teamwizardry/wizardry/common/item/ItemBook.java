package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemModBook;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.book.Book;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.spell.SpellUtils;
import com.teamwizardry.wizardry.api.spell.module.Module;
import com.teamwizardry.wizardry.client.gui.book.GuiBook;
import com.teamwizardry.wizardry.common.advancement.IPickupAchievement;
import com.teamwizardry.wizardry.common.advancement.ModAdvancements;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Demoniaque on 6/12/2016.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class ItemBook extends ItemModBook implements IPickupAchievement {

	public static Book BOOK = new Book("book");

	public ItemBook() {
		super("book");
		setMaxStackSize(1);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onScroll(MouseEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player == null) return;

		if (Keyboard.isCreated() && event.getDwheel() != 0 && player.isSneaking()) {

			for (EnumHand hand : EnumHand.values()) {
				ItemStack stack = player.getHeldItem(hand);

				if (stack.getItem() != ModItems.BOOK)
					continue;

				if (!ItemNBTHelper.getBoolean(stack, "has_spell", false))
					return;

				NBTTagList moduleList = ItemNBTHelper.getList(stack, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_STRING);
				if (moduleList == null)
					return;

				if (event.getDwheel() > 0) {

					List<List<Module>> spellModules = SpellUtils.deserializeModuleList(moduleList);
					List<ItemStack> spellItems = SpellUtils.getSpellItems(spellModules);

					int page = ItemNBTHelper.getInt(stack, "page", 0);

					int maxPages = 0;
					int row = 0;
					int column = 0;
					for (int i = 0; i < spellItems.size(); i++) {

						if (++column >= 3) {
							column = 0;
							row++;
						}

						if (row >= 9) {
							row = 0;
							maxPages++;

							if (maxPages > page) {
								ItemNBTHelper.setInt(stack, "page", page + 1);
							}
						}
					}

				} else {

					int page = ItemNBTHelper.getInt(stack, "page", 0);

					ItemNBTHelper.setInt(stack, "page", Math.max(page - 1, 0));
				}

				event.setCanceled(true);
			}
		}
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
