package com.teamwizardry.wizardry.common.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Module;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternRegistry;

import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

@TestInstance(Lifecycle.PER_CLASS)
public class ModuleLoaderTest
{
    @BeforeAll
    public void initPatterns()
    {
        PatternRegistry.addPatterns(Wizardry.MODID, Pair.of("test", new Pattern() {
            @Override public void run() {}
            @Override public void affectEntity(Entity entity) {}
            @Override public void affectBlock(BlockPos pos) {}
        }));
    }
    
    @Test
    public void loadModules()
    {
        List<Module> modules = ModuleLoader.loadModules("../src/test/resources/testModule.yaml");
        assertEquals(2, modules.size());
        
        Module zero = modules.get(0);
        assertEquals("wizardry:test", PatternRegistry.getName(zero.getPattern()));
        assertEquals("zero", zero.getName());
        assertNull(zero.getItem()); // Item is null due to item registry not existing out of lifecycle
        assertEquals(0xFF3C3C, zero.getPrimaryColor().getRGB());
        assertEquals(0xA10000, zero.getSecondaryColor().getRGB());
        assertEquals(2, zero.getTags().size());
        assertEquals("test", zero.getTags().get(0));
        assertEquals("module", zero.getTags().get(1));
        assertEquals(0, zero.getHiddenTags().size());
        
        Module one = modules.get(1);
        assertEquals("wizardry:test", PatternRegistry.getName(one.getPattern()));
        assertEquals("one", one.getName());
        assertNull(one.getItem()); // Item is null due to item registry not existing out of lifecycle
        assertEquals(0xFF3C3C, one.getPrimaryColor().getRGB());
        assertEquals(0xA10000, one.getSecondaryColor().getRGB());
        assertEquals(2, one.getTags().size());
        assertEquals("second", one.getTags().get(0));
        assertEquals("test", one.getTags().get(1));
        assertEquals(1, one.getHiddenTags().size());
        assertEquals("module", one.getHiddenTags().get(0));
    }
}
