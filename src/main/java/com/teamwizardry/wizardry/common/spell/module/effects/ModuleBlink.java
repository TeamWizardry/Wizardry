package com.teamwizardry.wizardry.common.spell.module.effects;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class ModuleBlink extends Module {
    public static final String COORD_SET = "Blink Coord Set";
    public static final String POS_X = "Blink X Coord";
    public static final String POS_Y = "Blink Y Coord";
    public static final String POS_Z = "Blink Z Coord";

    private boolean useCoord = false;
    private BlockPos pos = new BlockPos(0, 0, 0);

    public ModuleBlink() {
        attributes.addAttribute(Attribute.DISTANCE);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public String getDescription() {
        return "If no position is set, blink forward based on the power. Otherwise, teleport to the set location.";
    }

    @Override
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = super.getModuleData();
        compound.setBoolean(COORD_SET, useCoord);
        compound.setInteger(POS_X, pos.getX());
        compound.setInteger(POS_Y, pos.getY());
        compound.setInteger(POS_Z, pos.getZ());
        
        compound.setDouble(POWER, attributes.apply(Attribute.DISTANCE, 1));
        compound.setDouble(MANA, attributes.apply(Attribute.MANA, 10));
        compound.setDouble(BURNOUT, attributes.apply(Attribute.BURNOUT, 10));
        return compound;
    }

    public ModuleBlink setPos(BlockPos pos) {
        useCoord = true;
        this.pos = pos;
        return this;
    }
}