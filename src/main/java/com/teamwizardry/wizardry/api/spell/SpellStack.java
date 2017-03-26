package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.librarianlib.common.network.PacketHandler;
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper;
import com.teamwizardry.wizardry.api.Constants;
import com.teamwizardry.wizardry.api.WizardManager;
import com.teamwizardry.wizardry.common.network.PacketRenderSpell;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
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

	public static void runModules(@Nonnull Module mainModule, SpellData spell) {
		Entity caster = spell.getData(SpellData.DefaultKeys.CASTER);

		for (Module module : getAllModules(mainModule)) {
			if (caster instanceof EntityPlayer && !((EntityPlayer) caster).isCreative()) {
				if (WizardManager.getMana((EntityLivingBase) caster) < module.finalManaCost) {
					WizardManager.removeMana((int) module.finalManaCost, (EntityLivingBase) caster);
					WizardManager.addBurnout((int) module.finalBurnoutCost, (EntityLivingBase) caster);
				}
			}
			module.run(spell);

			Vec3d pos = spell.getData(SpellData.DefaultKeys.ORIGIN, caster != null ? caster.getPositionVector() : spell.getData(SpellData.DefaultKeys.TARGET_HIT, Vec3d.ZERO));

			if (spell.getData(SpellData.DefaultKeys.TARGET_HIT) == null) {
				PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(mainModule, spell),
						new NetworkRegistry.TargetPoint(spell.world.provider.getDimension(), pos.xCoord, pos.yCoord, pos.zCoord, 60));
			} else {
				Vec3d target = spell.getData(SpellData.DefaultKeys.TARGET_HIT);
				PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(mainModule, spell),
						new NetworkRegistry.TargetPoint(spell.world.provider.getDimension(), target.xCoord, target.yCoord, target.zCoord, 60));
			}

			if (caster instanceof EntityPlayer && !((EntityPlayer) caster).isCreative()) {
				WizardManager.removeMana((int) module.finalManaCost, (EntityLivingBase) caster);
				WizardManager.addBurnout((int) module.finalBurnoutCost, (EntityLivingBase) caster);
			}

			SpellCastEvent event = new SpellCastEvent(null, module, spell);
			MinecraftForge.EVENT_BUS.post(event);
		}
	}

	public static void runModules(@Nonnull ItemStack spellHolder, SpellData spell) {
		if (spell.world.isRemote) return;

		Entity caster = spell.getData(SpellData.DefaultKeys.CASTER);

		for (Module module : getModules(spellHolder)) {
			if (caster instanceof EntityPlayer && !((EntityPlayer) caster).isCreative()) {
				if (WizardManager.getMana((EntityLivingBase) caster) < module.finalManaCost) {
					WizardManager.removeMana((int) module.finalManaCost, (EntityLivingBase) caster);
					WizardManager.addBurnout((int) module.finalBurnoutCost, (EntityLivingBase) caster);
				}
			}
			module.run(spell);

			Vec3d pos = spell.getData(SpellData.DefaultKeys.ORIGIN, caster != null ? caster.getPositionVector() : spell.getData(SpellData.DefaultKeys.TARGET_HIT, Vec3d.ZERO));

			if (spell.getData(SpellData.DefaultKeys.TARGET_HIT) == null) {
				PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(spellHolder, spell),
						new NetworkRegistry.TargetPoint(spell.world.provider.getDimension(), pos.xCoord, pos.yCoord, pos.zCoord, 60));
			} else {
				Vec3d target = spell.getData(SpellData.DefaultKeys.TARGET_HIT);
				PacketHandler.NETWORK.sendToAllAround(new PacketRenderSpell(spellHolder, spell),
						new NetworkRegistry.TargetPoint(spell.world.provider.getDimension(), target.xCoord, target.yCoord, target.zCoord, 60));
			}

			if (caster instanceof EntityPlayer && !((EntityPlayer) caster).isCreative()) {
				WizardManager.removeMana((int) module.finalManaCost, (EntityLivingBase) caster);
				WizardManager.addBurnout((int) module.finalBurnoutCost, (EntityLivingBase) caster);
			}

			SpellCastEvent event = new SpellCastEvent(spellHolder, module, spell);
			MinecraftForge.EVENT_BUS.post(event);
		}
	}

	public static Set<Module> getModules(@Nonnull ItemStack spellHolder) {
		Set<Module> modules = new HashSet<>();

		NBTTagList list = ItemNBTHelper.getList(spellHolder, Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
		if (list == null) return modules;

		return getModules(list);
	}

	public static Set<Module> getModules(@Nonnull NBTTagCompound compound) {
		if (compound.hasKey(Constants.NBT.SPELL))
			return getModules(compound.getTagList(Constants.NBT.SPELL, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND));
		else return new HashSet<>();
	}

	public static Set<Module> getModules(@Nonnull NBTTagList list) {
		Set<Module> modules = new HashSet<>();
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound = list.getCompoundTagAt(i);
			Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
			if (module == null) continue;
			module.deserializeNBT(compound);
			Module.process(module);
			modules.add(module);
		}
		return modules;
	}

	public static Set<Module> getAllModules(@Nonnull NBTTagCompound compound) {
		Set<Module> modules = new HashSet<>();
		Set<Module> heads = getModules(compound);
		for (Module module : heads) {
			Module tempModule = module;
			while (tempModule != null) {
				modules.add(tempModule);
				tempModule = tempModule.nextModule;
			}
		}
		return modules;
	}

	public static Set<Module> getAllModules(@Nonnull Module module) {
		Set<Module> modules = new HashSet<>();
		Module tempModule = module;
		while (tempModule != null) {
			modules.add(tempModule);
			tempModule = tempModule.nextModule;
		}
		return modules;
	}

	public static Set<Module> getAllModules(@Nonnull ItemStack spellHolder) {
		Set<Module> modules = new HashSet<>();
		Set<Module> heads = getModules(spellHolder);
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
