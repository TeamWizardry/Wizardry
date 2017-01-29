package com.teamwizardry.wizardry.common.module.effects;

import com.teamwizardry.wizardry.api.spell.ITargettable;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.RegisterModule;
import com.teamwizardry.wizardry.api.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by LordSaad.
 */
@RegisterModule
public class ModuleEffectBlink extends Module implements ITargettable {

    public ModuleEffectBlink() {
	    process(this);
    }

    @NotNull
    @Override
    public ItemStack getRequiredStack() {
        return new ItemStack(Items.CHORUS_FRUIT_POPPED);
    }

	@Override
	public Color getColor() {
		return Color.MAGENTA;
	}

	@NotNull
	@Override
    public ModuleType getModuleType() {
        return ModuleType.EFFECT;
    }

    @NotNull
    @Override
    public String getID() {
        return "effect_blink";
    }

    @NotNull
    @Override
    public String getReadableName() {
        return "Blink";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Will teleport the target to the position of the spell";
    }

    @Override
    public double getManaToConsume() {
	    return 300;
    }

    @Override
    public double getBurnoutToFill() {
	    return 200;
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Vec3d target) {
	    if (caster != null) {
		    Utils.blink(caster, target.distanceTo(caster.getPositionVector()));
		    return true;
	    }
	    return false;
    }

    @Override
    public boolean run(@NotNull World world, @Nullable EntityLivingBase caster, @NotNull Entity target) {
	    if (caster != null) {
		    Utils.blink(caster, target.getPositionVector().distanceTo(caster.getPositionVector()));
		    return true;
	    }
	    return false;
    }

    @NotNull
    @Override
    public ModuleEffectBlink copy() {
        ModuleEffectBlink module = new ModuleEffectBlink();
        module.deserializeNBT(serializeNBT());
	    process(module);
	    return module;
    }
}
