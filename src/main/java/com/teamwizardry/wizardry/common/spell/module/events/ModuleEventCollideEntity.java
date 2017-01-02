package com.teamwizardry.wizardry.common.spell.module.events;

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
public class ModuleEventCollideEntity extends Module {

    public ModuleEventCollideEntity() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.ROTTEN_FLESH);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.EVENT;
    }

    @NotNull
    @Override
    public String getID() {
        return "on_collide_entity";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "On Collide Entity";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Triggered when the spell collides with an entity";
    }

    @NotNull
    @Override
    public Set<Module> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {
        return super.run(world, caster, spellStack);
    }

    @NotNull
    @Override
    public Module copy() {
        ModuleEventCollideEntity clone = new ModuleEventCollideEntity();
        clone.extraModifiers = extraModifiers;
        clone.children = children;
        return clone;
    }
}
