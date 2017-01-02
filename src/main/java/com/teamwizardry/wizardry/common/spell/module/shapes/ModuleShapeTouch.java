package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleShapeTouch extends Module {

    public ModuleShapeTouch() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.RABBIT_FOOT);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.SHAPE;
    }

    @NotNull
    @Override
    public String getID() {
        return "shape_touch";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Touch";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Will run the spell on the block hit";
    }

    @NotNull
    @Override
    public Set<Module> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {

        return false;
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleShapeTouch clone = new ModuleShapeTouch();
        clone.extraModifiers = extraModifiers;
        clone.children = children;
        return clone;
    }
}
