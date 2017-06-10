package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by LordSaad.
 */
public class SpellStack {

	public static Item fieldLineBreak = Items.WHEAT_SEEDS;
	public static Item fieldCodeSplitter = ModItems.FAIRY_DUST;
	public static Item codeLineBreak = ModItems.DEVIL_DUST;

	public static ArrayList<Item> identifiers = new ArrayList<>();

	// TODO: no...
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
		List<List<ItemStack>> branches = brancher(inventory, fieldCodeSplitter);
		if (branches.size() != 2) return; // If no fairy dust was found to split the spell twice, stop.

		// PROCESS FIELDS
		List<List<ItemStack>> fieldLines = brancher(branches.get(0), fieldLineBreak); // Get all the fields before the fairy dust.
		if (fieldLines.isEmpty()) return; // If no fields where found stop.

		for (List<ItemStack> fieldLine : fieldLines) {
			Deque<ItemStack> queue = new ArrayDeque<>(fieldLine);

			ItemStack stack = queue.pollFirst(); // Get the head module of the field. PollFirst removes it from the list too.
			if (!(identifiers.contains(stack.getItem())))
				continue; // If the field doesn't start with an identifier, skip.

			Module head = ModuleRegistry.INSTANCE.getModule(queue.pollFirst());
			if (head == null) continue;
			if (head instanceof IModifier) continue;

			// Everything else gets processed as a modifier to the head.
			for (ItemStack modifierStack : queue) {
				Module modifier = ModuleRegistry.INSTANCE.getModule(modifierStack);
				if (modifier == null) continue;
				if (!(modifier instanceof IModifier)) continue;

				((IModifier) modifier).apply(head);
				head.setMultiplier(head.getMultiplier() * ((IModifier) modifier).costMultiplier());
			}

			fields.put(stack.getItem(), head);
		}

		List<List<ItemStack>> lines = brancher(branches.get(1), codeLineBreak); // Get all the code lines of the second half of the spell.

		ArrayList<ArrayList<Module>> convertedLines = new ArrayList<>();
		for (List<ItemStack> line : lines) {

			ArrayList<Module> lineModules = new ArrayList<>();
			for (ItemStack stack : line) {
				Module module = fields.get(stack.getItem());
				if (module == null) continue;
				lineModules.add(module.copy());
			}

			convertedLines.add(lineModules);
		}

		for (ArrayList<Module> modules : convertedLines) {
			Deque<Module> deque = new ArrayDeque<>();
			deque.addAll(modules);

			for (Module ignored : modules) {
				if (deque.peekFirst() == deque.peekLast()) {
					compiled.add(deque.peekLast());
					break;
				}
				if (deque.peekLast() != null) {
					Module last = deque.pollLast();
					if (deque.peekLast() != null) {
						Module beforeLast = deque.peekLast();
						beforeLast.nextModule = last;
					}
				}
			}
		}

		// PROCESS COLOR
		for (Module module : compiled)
			module.processColor(module.nextModule);
	}

	@Nonnull
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

	public static void runSpell(@Nonnull Module module, @Nonnull SpellData spell) {
		spell.addData(SpellData.DefaultKeys.STRENGTH, module.calculateStrength(spell));
		module.castSpell(spell);
	}

	public static void runSpell(@Nonnull ItemStack spellHolder, @Nonnull SpellData spell) {
		runSpell(spellHolder, spell, null);
	}

	public static void runSpell(@Nonnull ItemStack spellHolder, @Nonnull SpellData spell, @Nullable EntityLivingBase player) {
		if (spell.world.isRemote) return;

		for (Module module : getModules(spellHolder)) {
			runSpell(module, spell);
		}

		int maxCooldown = 0;
		for (Module module : SpellStack.getAllModules(spellHolder))
			if (module.getCooldownTime() > maxCooldown) maxCooldown = module.getCooldownTime();
	}

	public static ArrayList<Module> getModules(@Nonnull ItemStack spellHolder) {
		ArrayList<Module> modules = new ArrayList<>();

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (list == null) return modules;

		return getModules(list);
	}

	public static ArrayList<Module> getModulesSoftly(@Nonnull ItemStack spellHolder) {
		ArrayList<Module> modules = new ArrayList<>();

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (list == null) return modules;

		return getModulesSoftly(list);
	}

	public static ArrayList<Module> getModules(@Nonnull NBTTagCompound compound) {
		if (compound.hasKey(Constants.NBT.SPELL))
			return getModules(compound.getTagList(Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND));
		else return new ArrayList<>();
	}

	public static ArrayList<Module> getModules(@Nonnull NBTTagList list) {
		ArrayList<Module> modules = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module == null) continue;
			module = module.copy();
			module.deserializeNBT(compound);
			modules.add(module);
		}
		return modules;
	}

	public static ArrayList<Module> getModulesSoftly(@Nonnull NBTTagList list) {
		ArrayList<Module> modules = new ArrayList<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module == null) continue;
			modules.add(module);
		}
		return modules;
	}

	public static ArrayList<Module> getAllModules(@Nonnull NBTTagCompound compound) {
		ArrayList<Module> modules = new ArrayList<>();
		ArrayList<Module> heads = getModules(compound);
		for (Module module : heads) {
			Module tempModule = module;
			while (tempModule != null) {
				modules.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules;
	}

	public static ArrayList<Module> getAllModules(@Nonnull Module module) {
		ArrayList<Module> modules = new ArrayList<>();
		Module tempModule = module;
		while (tempModule != null) {
			modules.add(tempModule);
			tempModule = tempModule.nextModule;
		}
		return modules;
	}

	public static ArrayList<Module> getAllModules(@Nonnull ItemStack spellHolder) {
		ArrayList<Module> modules = new ArrayList<>();
		ArrayList<Module> heads = getModules(spellHolder);
		for (Module module : heads) {
			Module tempModule = module;
			while (tempModule != null) {
				modules.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules;
	}

	public static ArrayList<Module> getAllModulesSoftly(@Nonnull ItemStack spellHolder) {
		ArrayList<Module> modules = new ArrayList<>();
		ArrayList<Module> heads = getModulesSoftly(spellHolder);
		for (Module module : heads) {
			Module tempModule = module;
			while (tempModule != null) {
				modules.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules;
	}
}
