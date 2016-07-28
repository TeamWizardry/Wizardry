package com.teamwizardry.wizardry.api.trackerobject;

import com.google.common.collect.Maps;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.module.ModuleRegistry;
import com.teamwizardry.wizardry.api.spell.IContinuousCast;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import javafx.collections.transformation.SortedList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LordSaad44
 *
 * This class is created when a spell is created, then tracks it and controls it everywhere
 */
public class SpellStack {

    // WIP
    // TODO: EXTREMELY TEMPORARY MESS THAT MUST BE IGNORED. jus.. just don't look at it for too long..

    /**
     * All the modules present inside of the original spell module.
     */
    private HashMap<ModuleType, SortedList<Module>> modules = Maps.newHashMap();

    /**
     * The first shape that was casted from the spell.
     */
    private Module primaryShape;

    private NBTTagCompound spell;

    /**
     * The player that originally casted the spell
     */
    private EntityPlayer player;

    /**
     * The entity that is casting the spell. IE: The zone entity
     */
    private Entity caster;

    public SpellStack(EntityPlayer player, Entity caster, NBTTagCompound spell) {
        this.player = player;
        this.caster = caster;
        primaryShape = ModuleRegistry.getInstance().getModuleById(spell.getInteger(Module.PRIMARY_SHAPE));
        this.spell = spell;
    }

    public void initSpell() {
        if (!(primaryShape instanceof IContinuousCast)) {
            // TODO: spawn projectile entity, zone entity, or do nothing like in self;
            // TODO: add "init" in some shape modules like projectile, zone, etc...
        }
    }

    public void castSpell() {
        SpellCastEvent event = new SpellCastEvent(spell, caster, player);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) primaryShape.cast(player, caster, spell);
    }

    /**
     * The original shape of the spell was casted from.
     *
     * @return An empty shape module.
     */
    public Module getPrimaryShape() {
        return primaryShape;
    }

    /**
     * TODO
     * <p>
     * Put active effects in a list whenever they are cast and use that list here
     * So add a cast method in here or something
     */
    public List<Module> getActiveEffects(Module module) {
        return null;
    }

    /**
     * TODO: Cache the color instead of calculating it every single time. This is gonna be called A LOT of times
     *
     * @return The color of the spell with respect to the current active effect or effects
     */
    public Color getSpellColor() {
        float r = -1, b = -1, g = -1;

        for (Module effect : modules.get(ModuleType.EFFECT)) {
            if (r == -1 && g == -1 && b == -1) {
                r = effect.getColor().r;
                g = effect.getColor().g;
                b = effect.getColor().b;
            } else {
                r = (r + effect.getColor().r) / 2;
                g = (g + effect.getColor().g) / 2;
                b = (b + effect.getColor().b) / 2;
            }
        }

        if (r == -1 && g == -1 && b == -1) return Color.WHITE;
        else return new Color(r, g, b);
    }

    public NBTTagCompound getSpell() {
        return spell;
    }

    public void setSpell(NBTTagCompound spell) {
        this.spell = spell;
    }
}
