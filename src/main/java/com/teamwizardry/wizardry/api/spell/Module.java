package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.api.capability.IWizardryCapability;
import com.teamwizardry.wizardry.api.capability.WizardryCapabilityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by LordSaad.
 */
public class Module implements INBTSerializable<NBTTagCompound> {

    /**
     * Extra information that can be editted and read by the module.
     * Used by modifiers.
     */
    public NBTTagCompound attributes = new NBTTagCompound();

    /**
     * The branches under this module in the stream of stacks provided in the recipe.
     */
    @Deprecated
    public Deque<Module> children = new ArrayDeque<>();

	/**
	 * The module that is to be ran from the run methods of the current module.
	 */
	@Nullable
	public Module nextModule;

    public Module() {
    }

    /**
     * The stack required during crafting to run this module.
     *
     * @return An itemstack representing the price.
     */
    @NotNull
    public ItemStack getRequiredStack() {
        return new ItemStack(Blocks.STONE);
    }

    /**
     * The type of module this module is.
     *
     * @return A ModuleType representing the type of module this is.
     */
    @NotNull
    public ModuleType getModuleType() {
        return ModuleType.SHAPE;
    }

    /**
     * A lower case snake_case string getID that reflects the module to identify it during serialization/deserialization.
     *
     * @return A lower case snake_case string.
     */
    @NotNull
    public String getID() {
        return "null_id";
    }

    /**
     * Represents the readable name of this module. Viewed in the worktable.
     *
     * @return A readable string representing it's name or title.
     */
    @NotNull
    public String getReadableName() {
        return "Null Module";
    }

    /**
     * The description of what this module does.
     *
     * @return A string representing what this module does.
     */
    @NotNull
    public String getDescription() {
        return "This module is null";
    }

    /**
     * A set of modifier modules that this module can be affected by.
     *
     * @return A set of modifier modules;
     */
    @NotNull
    public Set<Module> getCompatibleModifierModules() {
        return Collections.emptySet();
    }

    public double getManaToConsume() {
        return 10;
    }

    public double getBurnoutToFill() {
        return 10;
    }

    /**
     * Run the whatever is required on the SpellStack and then trigger the next step.
     *
     * @return whether this spell has succeeded or failed this step.
     */
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
        return false;
    }

    @Nullable
    public IWizardryCapability getCap(EntityLivingBase entity) {
        if (entity != null && entity instanceof EntityPlayer)
            return WizardryCapabilityProvider.get((EntityPlayer) entity);
        return null;
    }

    /**
     * Will apply modifiers to the head module given a stream of itemstacks representing the modifiers.
     *
     * @param modifiers The list of itemstacks representing a stream of modifiers to apply.
     */
    public void processModifiers(List<ItemStack> modifiers) {
        List<Module> finalModifiers = new ArrayList<>();

        Set<Integer> skipCells = new HashSet<>();
        for (int i = 0; i < modifiers.size() - 1; i++) {
            if (skipCells.contains(i)) continue;

            Module mainModifier = ModuleRegistry.INSTANCE.getModule(modifiers.get(i));
            if (mainModifier == null) break;
            if (!(mainModifier instanceof IModifier)) break;

            // PROCESS MODIFIERMODIFIERS
            Set<Integer> modifierModifiers = getModifierModifers(mainModifier, modifiers, i + 1);
            skipCells.addAll(modifierModifiers);

            for (int j : modifierModifiers) {
                Module modifierModifier = ModuleRegistry.INSTANCE.getModule(modifiers.get(j));
                if (modifierModifier != null
                        && modifierModifier instanceof IModifier)
                    ((IModifier) modifierModifier).apply(mainModifier);
            }

            finalModifiers.add(mainModifier);
        }

        // APPLY MODIIFERS TO MODULE
        for (Module modifier : finalModifiers) {
            if (!(modifier instanceof IModifier)) break;
            ((IModifier) modifier).apply(this);
        }
    }

    /**
     * WIll process modifiers of modifiers, example: PLUS modifier for EXTEND modifier.
     *
     * @param mainModifier The main modifier, in this example: EXTEND.
     * @param modifiers    The list of modifiers being processed.
     * @param index        The index of the modifiers list that's directly after the main modifier, example: Index of PLUS.
     * @return The set of indexes in the list to apply to the main modifier and to skip processing on the main module.
     */
    private Set<Integer> getModifierModifers(Module mainModifier, List<ItemStack> modifiers, int index) {
        Set<Integer> modifyingModifiers = new HashSet<>();
        for (int i = index; i < modifiers.size() - index - 1; i++) {
            Module modifier = ModuleRegistry.INSTANCE.getModule(modifiers.get(i));
            if (modifier == null) break;
            if (!(modifier instanceof IModifier)) break;
            if (mainModifier.getCompatibleModifierModules().contains(modifier))
                modifyingModifiers.add(i);
        }
        return modifyingModifiers;
    }

    @NotNull
    public Module copy() {
        return new Module();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("id", getID());

        compound.setTag("attributes", attributes);

        NBTTagList list = new NBTTagList();
        for (Module module : children) list.appendTag(module.serializeNBT());
        compound.setTag("children", list);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        attributes = nbt.getCompoundTag("attributes");

        NBTTagList list = nbt.getTagList("children", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            Module module = ModuleRegistry.INSTANCE.getModule(compound.getString("id"));
            if (module == null) continue;
            module.deserializeNBT(compound);
            children.add(module);
        }
    }
}
