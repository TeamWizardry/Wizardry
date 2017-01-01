package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.common.spell.module.events.ModuleEventAirBorne;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleEventCast;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleEventCollideBlock;
import com.teamwizardry.wizardry.common.spell.module.events.ModuleEventCollideEntity;
import com.teamwizardry.wizardry.common.spell.module.shapes.ModuleShapeBeam;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by LordSaad.
 */
public class ModuleRegistry {

    public static ModuleRegistry INSTANCE = new ModuleRegistry();

    public Set<IModule> modules = new HashSet<>();

    private ModuleRegistry() {
        registerModule(new ModuleEventCollideBlock());
        registerModule(new ModuleEventCollideEntity());
        registerModule(new ModuleEventAirBorne());
        registerModule(new ModuleEventCast());

        registerModule(new ModuleShapeBeam());
    }

    public void registerModule(IModule module) {
        modules.add(module);
    }

    @Nullable
    public IModule getModule(String id) {
        for (IModule module : modules) if (module.getID().equals(id)) return module;
        return null;
    }

    @Nullable
    public IModule getModule(ItemStack itemStack) {
        for (IModule module : modules)
            if (ItemStack.areItemStacksEqual(itemStack, module.getRequiredStack())) return module;
        return null;
    }

    @Nullable
    public IModule getModule(Item item) {
        for (IModule module : modules) if (item == module.getRequiredStack().getItem()) return module;
        return null;
    }

    @NotNull
    public Set<IModule> getModules(ModuleType type) {
        Set<IModule> modules = new HashSet<>();
        for (IModule module : this.modules) if (module.getModuleType() == type) modules.add(module);
        return modules;
    }
}
