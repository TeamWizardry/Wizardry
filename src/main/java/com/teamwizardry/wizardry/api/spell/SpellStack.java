package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by LordSaad.
 */
public class SpellStack {

	public static Set<Item> identifiers = new HashSet<>();

	static {
		identifiers.add(Items.GOLD_NUGGET);
		identifiers.add(Items.ROTTEN_FLESH);
		identifiers.add(Items.SPECKLED_MELON);
		identifiers.add(ModItems.FAIRY_WINGS);
		identifiers.add(Items.BEETROOT);
		identifiers.add(Items.SPIDER_EYE);
		identifiers.add(Items.PUMPKIN_SEEDS);
		identifiers.add(Items.GHAST_TEAR);
		identifiers.add(ModItems.FAIRY_IMBUED_APPLE);
		identifiers.add(Items.STRING);
		identifiers.add(Items.BONE);
	}

	public HashMap<Item, Module> fields = new HashMap<>();
	public ArrayList<Module> compiled = new ArrayList<>();

	public SpellStack(List<ItemStack> inventory) {
		List<List<ItemStack>> branches = brancher(inventory, ModItems.FAIRY_DUST);
		if (branches.size() != 2) return; // If no fairy dust was found to split the spell twice, stop.

		// PROCESS FIELDS
		List<List<ItemStack>> fieldLines = brancher(branches.get(0), Items.WHEAT_SEEDS); // Get all the fields before the fairy dust.
		if (fieldLines.isEmpty()) return; // If no fields where found stop.

		for (List<ItemStack> fieldLine : fieldLines) {
			Deque<ItemStack> queue = new ArrayDeque<>(fieldLine);

			ItemStack stack = queue.pollFirst(); // Get the head module of the field. PollFirst removes it from the list too.
			if (!(identifiers.contains(stack.getItem())))
				continue; // If the field doesn't start with an identifier, skip.

			Module head = ModuleRegistry.INSTANCE.getModule(queue.pollFirst());
			if (head == null) continue;
			if (head instanceof IModifier) continue;

			head.processModifiers(new ArrayList<>(queue)); // Everything else gets processed as a modifier.

			fields.put(stack.getItem(), head);
		}

		List<List<ItemStack>> lines = brancher(branches.get(1), ModItems.DEVIL_DUST); // Get all the code lines of the second half of the spell.

		// PROCESS CHILDREN OF LINE HEADS
		for (List<ItemStack> line : lines) {
			Deque<ItemStack> queue = new ArrayDeque<>(line);

			for (ItemStack ignored : line) {
				if (queue.size() <= 1) break;
				if (fields.containsKey(queue.peekLast().getItem())) {
					Module lastField = fields.get(queue.pollLast().getItem());
					if (fields.containsKey(queue.peekLast().getItem())) {
						Module beforeLastField = fields.get(queue.peekLast().getItem());
						beforeLastField.nextModule = lastField;
					}
				}
			}

			if (queue.peekFirst() != null && fields.containsKey(queue.peekFirst().getItem()))
				compiled.add(fields.get(queue.peekFirst().getItem()));
		}
	}

	@NotNull
	public static List<List<ItemStack>> brancher(List<ItemStack> inventory, Item identifier) {
		List<List<ItemStack>> branches = new ArrayList<>();
		List<ItemStack> temp = new ArrayList<>();
		for (ItemStack stack : inventory) {
			if (ItemStack.areItemsEqual(new ItemStack(identifier), stack)) {
				if (!temp.isEmpty()) branches.add(temp);
				temp = new ArrayList<>();
			} else temp.add(stack);
		}
		if (!temp.isEmpty()) branches.add(temp);
		return branches;
	}

	public static void runModules(ItemStack spellHolder, World world, @Nullable EntityLivingBase entityLiving) {
		if ((spellHolder == null) || (world == null)) return;

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND, false);
		if (list == null) return;

		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module == null) continue;
			module.deserializeNBT(compound);
			module.run(world, entityLiving);
		}
	}
}
