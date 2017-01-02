package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
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
public class ModuleShapeSelf extends Module {

    public ModuleShapeSelf() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.DIAMOND);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.SHAPE;
    }

    @NotNull
    @Override
    public String getID() {
        return "shape_self";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Self";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Will run the spell on the caster";
    }

    @NotNull
    @Override
    public Set<Module> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
        return super.run(world, caster);
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleShapeSelf clone = new ModuleShapeSelf();
        clone.children = children;
        return clone;
    }
}
