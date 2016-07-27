package com.teamwizardry.wizardry.api.trackerobject;

import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;

import static com.teamwizardry.wizardry.api.module.Module.MODULES;

/**
 * Created by saad4 on 27/7/2016.
 */
public class SpellTrackerAlpha {

    private ArrayList<Module> effects = new ArrayList<>();
    private ArrayList<Module> events = new ArrayList<>();
    private ArrayList<Module> modifiers = new ArrayList<>();
    private ArrayList<Module> shapes = new ArrayList<>();
    private ArrayList<Module> booleans = new ArrayList<>();
    private ArrayList<NBTTagCompound> modules = new ArrayList<>();
    private Module mainShape;
    private Module currentActiveShape;
    private int activeShapeQueue = 0;
    private NBTTagCompound spellNBT;

    public SpellTrackerAlpha(NBTTagCompound spell) {
        this.spellNBT = spell;
        NBTTagList modules = spell.getTagList(MODULES, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < modules.tagCount(); i++) this.modules.add(modules.getCompoundTagAt(i));
        currentActiveShape = mainShape = ModuleList.INSTANCE.modules.get(spell.getString(Module.CLASS)).construct();

        // TODO: Fill in effects, events, modifiers, shapes, and booleans
    }

    public void initNextShape() {
        if (shapes.size() > activeShapeQueue) {
            activeShapeQueue++;
            currentActiveShape = shapes.get(activeShapeQueue);
        }
    }

    public ArrayList<Module> getEffects() {
        return effects;
    }

    public ArrayList<Module> getModifiers() {
        return modifiers;
    }

    public ArrayList<Module> getShapes() {
        return shapes;
    }

    public ArrayList<Module> getBooleans() {
        return booleans;
    }

    public Module getMainShape() {
        return mainShape;
    }

    public Module getCurrentActiveShape() {
        return currentActiveShape;
    }

    public ArrayList<Module> getEvents() {
        return events;
    }
}
