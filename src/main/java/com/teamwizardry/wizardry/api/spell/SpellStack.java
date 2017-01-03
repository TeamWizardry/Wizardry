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

    // TRY 2
    public static Set<Item> identifiers = new HashSet<>();

    static {
        identifiers.add(Items.GOLD_NUGGET);
        identifiers.add(ModItems.DEVIL_DUST);
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

    // TRY 1
    @NotNull
    public Deque<ItemStack> children = new ArrayDeque<>();
    public HashMap<Item, Module> fields = new HashMap<>();
    public ArrayList<Module> compiled = new ArrayList<>();

    public SpellStack(List<ItemStack> inventory) {
        List<List<ItemStack>> branches = brancher(inventory, ModItems.FAIRY_DUST);
        if (branches.size() != 2) return;

        // PROCESS FIELDS
        List<List<ItemStack>> fieldLines = brancher(branches.get(0), Items.WHEAT_SEEDS);
        if (fieldLines.isEmpty()) return;

        for (List<ItemStack> fieldLine : fieldLines) {
            if (!(identifiers.contains(fieldLine.get(0).getItem()))) continue;

            Module head = ModuleRegistry.INSTANCE.getModule(fieldLine.get(1));
            if (head == null) continue;
            if (head instanceof IModifier) continue;

            List<ItemStack> modifiers = new ArrayList<>();
            for (int i = 2; i < fieldLine.size() - 1; i++) {
                Module modifier = ModuleRegistry.INSTANCE.getModule(fieldLine.get(i));
                if (modifier instanceof IModifier)
                    modifiers.add(fieldLine.get(i));
            }

            head.processModifiers(modifiers);

            fields.put(fieldLine.get(0).getItem(), head);
        }

        // PROCESS CHILDREN OF LINE HEADS
        List<List<ItemStack>> lines = brancher(branches.get(1), ModItems.DEVIL_DUST);
        prime:
        for (List<ItemStack> line : lines) {
            if (line.isEmpty()) continue;
            ItemStack headStack = line.get(0);
            if (!fields.containsKey(headStack.getItem())) continue;

            Module head = fields.get(headStack.getItem());
            if (head == null) continue;
            if (head.getModuleType() != ModuleType.SHAPE) continue;

            for (int i = 1; i < line.size() - 1; i++) {
                if (!fields.containsKey(line.get(i).getItem())) continue prime;

                Module child = fields.get(headStack.getItem());
                if (child == null) continue;
                head.children.add(child);
            }
            compiled.add(head);
        }

        // REPROCESS CHILDREN FOR ALL CHILDREN MODULES
        for (Module head : compiled) {

            Deque<Module> children = new ArrayDeque<>(head.children);
            ArrayList<Module> childrenList = new ArrayList<>(children);

            for (int i = 0; i < childrenList.size() - 1; i++) {
                Module module = children.pop();
                module.children = children;
            }
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
