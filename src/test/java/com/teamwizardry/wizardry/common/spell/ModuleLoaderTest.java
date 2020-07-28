package com.teamwizardry.wizardry.common.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternEffect;
import com.teamwizardry.wizardry.api.spell.PatternShape;
import com.teamwizardry.wizardry.common.spell.loading.ModuleLoader;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

@TestInstance(Lifecycle.PER_CLASS)
public class ModuleLoaderTest
{
    private HashMap<ResourceLocation, Pattern> testPatterns = new HashMap<>();
    private List<Module> modules;
    
    private ResourceLocation shapeLoc = new ResourceLocation(Wizardry.MODID, "test_shape");
    private ResourceLocation effectLoc = new ResourceLocation(Wizardry.MODID, "test_effect");
    
    @BeforeAll
    public void initTest()
    {
        testPatterns.put(shapeLoc, new PatternShape() {
            @Override public void run(Function<BlockPos, Boolean> shouldAffectBlock, Function<Entity, Boolean> shouldAffectEntity) {}
            @Override public void affectEntity(Entity entity) {}
            @Override public void affectBlock(BlockPos pos) {}
        });
        
        testPatterns.put(effectLoc, new PatternEffect() {
            @Override public void run(Function<BlockPos, Boolean> shouldAffectBlock, Function<Entity, Boolean> shouldAffectEntity) {}
            @Override public void affectEntity(Entity entity) {}
            @Override public void affectBlock(BlockPos pos) {}
        });
        
        try
        {
            modules = ModuleLoader.loadModules(new FileInputStream(new File("src/test/resources/testModule.yaml")), testPatterns::get, str -> null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testModuleCount()
    {
        assertEquals(2, modules.size());
    }
    
    @Test
    public void testModuleShape()
    {
        assertTrue(modules.get(0) instanceof ModuleShape);
        ModuleShape shape = (ModuleShape) modules.get(0);
        assertEquals(testPatterns.get(shapeLoc), shape.getPattern());
        assertEquals("zero", shape.getName());
        assertNull(shape.getItem());
        assertEquals(4, shape.getAttributeRanges().size());
        assertEquals(2, shape.getTags().size());
        assertEquals("test", shape.getTags().get(0));
        assertEquals("module", shape.getTags().get(1));
        assertEquals(0, shape.getHiddenTags().size());
    }
    
    @Test
    public void testModuleEffect()
    {
        assertTrue(modules.get(1) instanceof ModuleEffect);
        ModuleEffect effect = (ModuleEffect) modules.get(1);
        assertEquals(testPatterns.get(effectLoc), effect.getPattern());
        assertEquals("one", effect.getName());
        assertNull(effect.getItem());
        assertEquals(0xFF123456, effect.getPrimaryColor().getRGB());
        assertEquals(0xFFABCDEF, effect.getSecondaryColor().getRGB());
        assertEquals(2, effect.getTags().size());
        assertEquals("second", effect.getTags().get(0));
        assertEquals("test", effect.getTags().get(1));
        assertEquals(1, effect.getHiddenTags().size());
        assertEquals("module", effect.getHiddenTags().get(0));
    }
}
