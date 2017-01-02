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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleShapeProjectile extends Module {

    public ModuleShapeProjectile() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.NETHER_WART);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.SHAPE;
    }

    @NotNull
    @Override
    public String getID() {
        return "shape_projectile";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Projectile";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Will launch the spell as a projectile in the direction the caster is looking.";
    }

    @NotNull
    @Override
    public Set<Module> getCompatibleModifierModules() {
        Set<Module> modules = new HashSet<>();
        // TODO
        //modules.add()
        return modules;
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {
        // TODO: Spawn Spell Entity
        return true;
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleShapeProjectile clone = new ModuleShapeProjectile();
        clone.extraModifiers = extraModifiers;
        clone.children = children;
        return clone;
    }
}
