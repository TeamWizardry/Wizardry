package com.teamwizardry.wizardry.common.spell.module.shapes;

import com.teamwizardry.wizardry.api.spell.IModule;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellStack;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleShapeBeam extends IModule {

    public ModuleShapeBeam() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(ModItems.UNICORN_HORN);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.SHAPE;
    }

    @NotNull
    @Override
    public String getID() {
        return "shape_beam";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Beam";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Will run the spell via a beam emanating from the caster";
    }

    @NotNull
    @Override
    public Set<IModule> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {

        return false;
    }

    @NotNull
    @Override
    public IModule copy() {
        ModuleShapeBeam clone = new ModuleShapeBeam();
        clone.modifierModules = modifierModules;
        return clone;
    }
}
