package me.lordsaad.wizardry.api.modules;

import me.lordsaad.wizardry.Wizardry;
import me.lordsaad.wizardry.api.modules.attribute.Attribute;
import me.lordsaad.wizardry.api.modules.attribute.AttributeMap;
import me.lordsaad.wizardry.spells.modules.ModuleType;
import me.lordsaad.wizardry.spells.modules.modifiers.IModifier;
import me.lordsaad.wizardry.spells.modules.modifiers.IRuntimeModifier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 6/21/2016.
 * <pre></pre>
 * Edited by Escapee from 6/22/2016
 */
public abstract class Module {
    public static final String CLASS = "Class";
    public static final String MODULES = "Modules";
    public static final String POWER = "Power";
    public static final String DURATION = "Duration";
    public static final String SILENT = "Silent";
    public static final String MANA = "Mana";
    public static final String BURNOUT = "Burnout";
    public static final String RADIUS = "Radius";

    protected boolean canHaveChildren = true;
    public AttributeMap attributes = new AttributeMap();
    public List<IRuntimeModifier> runtimeModifiers = new ArrayList<>();

    { /* attributes/parsing */ }

    public Module() {
        attributes.addAttribute(Attribute.COST);
        attributes.addAttribute(Attribute.BURNOUT);
    }

    /**
     * Determine what type of module this is: An EFFECT, EVENT, MODIFIER, SHAPE, or BOOLEAN
     *
     * @return The module's {@link ModuleType}
     */
    public abstract ModuleType getType();

    /**
     * Generates an {@link NBTTagCompound} containing information about the module and its effects, as well as any connected modules.
     *
     * @return An {@code NBTTagCompound} containing information on the module and all connected modules
     */
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString(CLASS, this.getClass().getName());
        NBTTagList list = new NBTTagList();
        for (IRuntimeModifier modifier : runtimeModifiers) {
            list.appendTag(modifier.saveToNBT());
        }
        return compound;
    }

    /**
     * Returns the default {@link RecourseLocation} for this module's icon.
     *
     * @return A {@code ResourceLocation} with the location {@code Wizardry:this.class.simpleName}
     */
    public ResourceLocation getIcon() {
        return new ResourceLocation(Wizardry.MODID, this.getClass().getSimpleName());
    }

    /**
     * Handle a child module {@code other}
     *
     * @param other the child module
     * @return if the module was handled
     */
    public boolean accept(Module other) {
        if (other instanceof IModifier) {
            IModifier modifier = ((IModifier) other);
            attributes.beginCaputure();
            modifier.apply(attributes);

            if (modifier.doesFallback() && attributes.didHaveInvalid()) {
                attributes.endCapture(false); // discard changes and don't return true so it passes on to subclass
            } else {
                attributes.endCapture(true); // save changes
                return true;// we don't want to handle the module normally, so return that we handled it
            }
        }
        if (other instanceof IRuntimeModifier) {
            runtimeModifiers.add((IRuntimeModifier) other);
        }
        return false;
    }

    public boolean canHaveChildren() {
        return canHaveChildren;
    }
}