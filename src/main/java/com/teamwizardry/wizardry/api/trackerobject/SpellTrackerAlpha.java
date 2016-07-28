package com.teamwizardry.wizardry.api.trackerobject;

import com.google.common.collect.Maps;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.api.spell.SpellEntity;
import com.teamwizardry.wizardry.api.spell.event.SpellCastEvent;
import com.teamwizardry.wizardry.common.spell.parsing.Parser;
import javafx.collections.transformation.SortedList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saad4 on 27/7/2016.
 */
public class SpellTrackerAlpha {

    // WIP
    // TODO: EXTREMELY TEMPORARY MESS THAT MUST BE IGNORED. jus.. just don't look at it for too long..

    /**
     * All the modules present inside of the original spell module.
     */
    private HashMap<ModuleType, SortedList<Module>> modules = Maps.newHashMap();

    /**
     * The player that originally casted the spell. Can be FakePlayer.
     */
    private EntityPlayer player;

    /**
     * The SpellEntity in the world.
     */
    private SpellEntity entity;

    /**
     * The original module of the spell
     */
    private Module mainModule;

    /**
     * The first shape that was casted from the spell.
     */
    private Module primaryShape;

    /**
     * The current shape that is running in the spell chain.
     */
    private Module currentActiveShape;

    /**
     * The queue of the spell shape chain to progress through.
     */
    private int activeShapeQueue = 0;

    /**
     *
     */
    private NBTTagCompound spellCompound;

    public SpellTrackerAlpha(Module module) {
        modules = Parser.parseModuleToLists(module);
        spellCompound = module.getModuleData();
        primaryShape = currentActiveShape = modules.get(ModuleType.SHAPE).get(0);
        mainModule = module;
    }

    /**
     * Will progress through the spell's shape chain by 1.
     *
     * If currentActiveShape is null, that means the spell is complete and has progressed
     * through all available chains.
     */
    public void initNextShape() {
        if (modules.get(ModuleType.SHAPE).size() > activeShapeQueue) {
            activeShapeQueue++;
            currentActiveShape = modules.get(ModuleType.SHAPE).get(activeShapeQueue);
        } else currentActiveShape = null;
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
     * The current shape being used in the spell chain.
     * @return An empty shape module.
     */
    public Module getCurrentActiveShape() {
        return currentActiveShape;
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

    @SubscribeEvent
    public void tickSpell(TickEvent.WorldTickEvent event) {
        NBTTagCompound compound = currentActiveShape.getModuleData();
        if (!modules.isEmpty() && compound.hasKey(Module.CLASS) && currentActiveShape != null) {

            SpellCastEvent cast = new SpellCastEvent(compound, entity, player);
            MinecraftForge.EVENT_BUS.post(cast);
            if (!cast.isCanceled()) currentActiveShape.cast(player, entity, this);
        }
    }

    public NBTTagCompound getSpellCompound() {
        return spellCompound;
    }

    public void setSpellCompound(NBTTagCompound spellCompound) {
        this.spellCompound = spellCompound;
    }
}
