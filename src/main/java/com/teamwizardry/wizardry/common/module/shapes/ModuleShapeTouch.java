package com.teamwizardry.wizardry.common.module.shapes;

import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {

        return false;
    }
}
