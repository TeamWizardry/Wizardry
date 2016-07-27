package com.teamwizardry.wizardry.api.trackerobject;

import com.google.common.collect.Maps;
import com.teamwizardry.librarianlib.api.util.misc.Color;
import com.teamwizardry.wizardry.api.module.Module;
import com.teamwizardry.wizardry.api.spell.ModuleType;
import com.teamwizardry.wizardry.common.spell.parsing.Parser;
import javafx.collections.transformation.SortedList;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saad4 on 27/7/2016.
 */
public class SpellTrackerAlpha {

    // WIP
    // TODO: EXTREMELY TEMPORARY MESS THAT MUST BE IGNORED. jus.. just don't look at it for too long..

    private HashMap<ModuleType, SortedList<Module>> modules = Maps.newHashMap();

    private Module mainModule;
    private Module primaryShape;
    private Module currentActiveShape;
    private int activeShapeQueue = 0;

    public SpellTrackerAlpha(Module module) {
        modules = Parser.parseModuleToLists(module);
        primaryShape = mainModule = modules.get(ModuleType.SHAPE).get(0);
        mainModule = module;
    }

    public void initNextShape() {
        if (modules.get(ModuleType.SHAPE).size() > activeShapeQueue) {
            activeShapeQueue++;
            currentActiveShape = modules.get(ModuleType.SHAPE).get(activeShapeQueue);
        }
    }

    public Module getPrimaryShape() {
        return primaryShape;
    }

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
}
