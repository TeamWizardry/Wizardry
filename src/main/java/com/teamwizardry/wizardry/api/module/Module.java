package com.teamwizardry.wizardry.api.module;

import com.teamwizardry.librarianlib.sprite.Sprite;
import com.teamwizardry.librarianlib.sprite.Texture;
import com.teamwizardry.librarianlib.util.Color;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.module.attribute.Attribute;
import com.teamwizardry.wizardry.api.module.attribute.AttributeMap;
import com.teamwizardry.wizardry.api.spell.IModifier;
import com.teamwizardry.wizardry.api.spell.IRuntimeModifier;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.trackerobject.SpellStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 6/21/2016.
 * <p>
 * Edited by Escapee from 6/22/2016
 */
public abstract class Module {
    public static final Texture STATIC_ICON_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/modules/static.png"));
    public static final Texture ANIMATED_ICON_SHEET = new Texture(new ResourceLocation(Wizardry.MODID, "textures/gui/worktable/modules/animated.png"));

    public static final String SHAPE = "Shape";
    public static final String TYPE = "Type";
    public static final String MODULES = "Modules";
    public static final String POWER = "Power";
    public static final String DURATION = "Duration";
    public static final String RADIUS = "Radius";
    public static final String PIERCE = "Pierce";
    public static final String SILENT = "Silent";
    public static final String SPEED = "Speed";
    public static final String KNOCKBACK = "Knockback";
    public static final String PROJ_COUNT = "Projectile Count";
    public static final String SCATTER = "Scatter";
    public static final String CRIT_CHANCE = "Crit Chance";
    public static final String CRIT_DAMAGE = "Crit Damage";
    public static final String DISTANCE = "Distance";
    public static final String DAMAGE = "Damage";

    public static final String MANA = "Mana";
    public static final String BURNOUT = "Burnout";

    public AttributeMap attributes = new AttributeMap();
    public ItemStack stack;
    protected boolean canHaveChildren = true;
    private List<Module> children = new ArrayList<>();
    private int id;
    private Sprite staticIcon = STATIC_ICON_SHEET.getSprite(this.getClass().getSimpleName(), 16, 16), animatedIcon = ANIMATED_ICON_SHEET.getSprite(this.getClass().getSimpleName(), 16, 16);
    private String description = "<-NULL->";
    private String displayName = "<-NULL->";

    public Module(ItemStack stack) {
        this.stack = stack;
        attributes.addAttribute(Attribute.MANA);
        attributes.addAttribute(Attribute.BURNOUT);
    }


    /**
     * Determine what type of module this is: An EFFECT, EVENT, MODIFIER, SHAPE,
     * or BOOLEAN
     *
     * @return The module's {@link ModuleType}
     */
    public abstract ModuleType getType();

    /**
     * Generates an {@code NBTTagCompound} containing information about the
     * module and its effect, as well as any connected module.
     *
     * @return An {@link NBTTagCompound} containing information on the module
     * and all connected module
     */
    public NBTTagCompound getModuleData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger(SHAPE, ModuleRegistry.getInstance().getModuleId(this));
        compound.setString(TYPE, getType().toString());
        NBTTagList list = new NBTTagList();
        for (Module module : children) list.appendTag(module.getModuleData());
        compound.setTag(MODULES, list);
        return compound;
    }

    /**
     * Gets the static {@link Sprite} to be used for rendering this module in GUIs
     *
     * @return The current {@link ResourceLocation}
     */
    public Sprite getStaticIcon() {
        return staticIcon;
    }

    /**
     * Sets the static {@code Sprite} for this module
     *
     * @param sprite The new {@link Sprite}
     */
    public void setStaticIcon(Sprite sprite) {
        staticIcon = sprite;
    }

    /**
     * Gets the animated {@link Sprite} to be used for rendering this module in GUIs
     *
     * @return The current {@link ResourceLocation}
     */
    public Sprite getAnimatedIcon() {
        return animatedIcon;
    }

    /**
     * Sets the animated {@code Sprite} for this module
     *
     * @param sprite The new {@link Sprite}
     */
    public void setAnimatedIcon(Sprite sprite) {
        animatedIcon = sprite;
    }

    /**
     * Get the description of this module which describes what it does.
     * <p>
     * Override it to return a custom description
     *
     * @return The current {@link String} set for this module which describes
     * what it does
     */
    public String getDescription() {
        return description;
    }

    /**
     * Handle a child module {@code other}
     *
     * @param other the child module
     * @return if the module was handled
     */
    public boolean accept(Module other) {
    	if (other == null || other == this) return false;
        boolean accept = false;
        switch (this.getType()) {
            case BOOLEAN:
                if (!(other instanceof IModifier)) {
                    children.add(other);
                    accept = true;
                }
                return accept;
            case EFFECT:
                if (other instanceof IModifier)
                    accept = addModifier((IModifier) other);
                if (other instanceof IRuntimeModifier) {
                    children.add(other);
                    accept = true;
                }
                return accept;
            case EVENT:
                if (other.getType() == ModuleType.SHAPE || other.getType() == ModuleType.EFFECT) {
                    children.add(other);
                    return true;
                }
                break;
            case MODIFIER:
                if (other instanceof IModifier)
                    accept = addModifier((IModifier) other);
                if (other instanceof IRuntimeModifier) {
                    children.add(other);
                    accept = true;
                }
                return accept;
            case SHAPE:
                if (other instanceof IModifier) {
                    accept = addModifier((IModifier) other);
                    if (other instanceof IRuntimeModifier) {
                        children.add(other);
                        accept = true;
                    }
                } else {
                    children.add(other);
                    accept = true;
                }
                return accept;
        }
        return false;
    }

    private boolean addModifier(IModifier modifier) {
        attributes.beginCaputure();
        modifier.apply(attributes);

        if (modifier.doesFallback() && attributes.didHaveInvalid())
            attributes.endCapture(false); // discard changes and don't
            // return true so it passes on
            // to subclass
        else {
            attributes.endCapture(true); // save changes
            return true;// we don't want to handle the module normally, so
            // return that we handled it
        }
        return false;
    }

    public boolean canHaveChildren() {
        return canHaveChildren;
    }

    /**
     * Causes the module to be cast, producing the corresponding effect.
     *
     * @param player The original caster of the spell
     * @param caster The current entity using the spell. This can be the player, a
     *               spell entity, or any target
     * @param spell  The spell's data
     * @return Whether or not the module was successfully cast. Used to evaluate
     * conditional modules.
     */
    public abstract boolean cast(EntityPlayer player, Entity caster, NBTTagCompound spell, SpellStack stack);

    /**
     * Will return the display name of the module
     *
     * @return the display name of the module
     */
    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return Color.WHITE;
    }

    public int getId() {
        return ModuleRegistry.getInstance().getModuleId(this);
    }

    public void setId(int id0) {
        id = id0;
    }

    public List<Module> getChildren() { return children; }

    public boolean hasChildren() { return !children.isEmpty(); }
}