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
public class ModuleShapeProjectile extends Module {

    public ModuleShapeProjectile() {
        process();
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

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster) {
        // TODO: Spawn Spell Entity
        return true;
    }

    @NotNull
    @Override
    public ModuleShapeProjectile copy() {
        ModuleShapeProjectile module = new ModuleShapeProjectile();
        module.deserializeNBT(serializeNBT());
        module.process();
        return module;
    }
}
