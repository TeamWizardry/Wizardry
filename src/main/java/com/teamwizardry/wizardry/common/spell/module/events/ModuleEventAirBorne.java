package com.teamwizardry.wizardry.common.spell.module.events;

import com.teamwizardry.wizardry.api.spell.IModule;
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
public class ModuleEventAirBorne extends IModule {

    public ModuleEventAirBorne() {
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.FEATHER);
    }

    @NotNull
    @Override
    public ModuleType getModuleType() {
        return ModuleType.EVENT;
    }

    @NotNull
    @Override
    public String getID() {
        return "while_air_borne";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "While Air Borne";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Triggered continuously while the spell is flying in the air.";
    }

    @NotNull
    @Override
    public Set<IModule> getCompatibleModifierModules() {
        return super.getCompatibleModifierModules();
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull SpellStack spellStack) {
        return super.run(world, caster, spellStack);
    }

    @NotNull
    @Override
    public IModule copy() {
        ModuleEventAirBorne clone = new ModuleEventAirBorne();
        clone.modifierModules = modifierModules;
        clone.extraModifiers = extraModifiers;
        return clone;
    }
}
