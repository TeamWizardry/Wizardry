package com.teamwizardry.wizardry.api.spell;

import com.teamwizardry.wizardry.common.module.events.ModuleEventAlongPath;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCast;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCollideBlock;
import com.teamwizardry.wizardry.common.module.events.ModuleEventCollideEntity;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierExtend;
import com.teamwizardry.wizardry.common.module.modifiers.ModuleModifierPlus;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeBeam;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeProjectile;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeSelf;
import com.teamwizardry.wizardry.common.module.shapes.ModuleShapeTouch;
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

    public Set<Module> modules = new HashSet<>();

    private ModuleRegistry() {
        registerModule(new ModuleEventCollideBlock());
        registerModule(new ModuleEventCollideEntity());
        registerModule(new ModuleEventAlongPath());
        registerModule(new ModuleEventCast());

        registerModule(new ModuleShapeBeam());
        registerModule(new ModuleShapeProjectile());
        registerModule(new ModuleShapeSelf());
        registerModule(new ModuleShapeTouch());

        registerModule(new ModuleModifierExtend());
        registerModule(new ModuleModifierPlus());
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    @Nullable
    public Module getModule(String id) {
        for (Module module : modules) if (module.getID().equals(id)) return module;
        return null;
    }

    @Nullable
    public Module getModule(ItemStack itemStack) {
        for (Module module : modules)
            if (ItemStack.areItemStacksEqual(itemStack, module.getRequiredStack())) return module.copy();
        return null;
    }

    @Nullable
    public Module getModule(Item item) {
        for (Module module : modules) if (item == module.getRequiredStack().getItem()) return module.copy();
        return null;
    }

    @NotNull
    public Set<Module> getModules(ModuleType type) {
        Set<Module> modules = new HashSet<>();
        for (Module module : this.modules) if (module.getModuleType() == type) modules.add(module.copy());
        return modules;
    }
}
