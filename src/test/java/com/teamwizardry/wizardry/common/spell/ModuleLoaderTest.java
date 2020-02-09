package com.teamwizardry.wizardry.common.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.teamwizardry.wizardry.api.spell.Module;

@TestInstance(Lifecycle.PER_CLASS)
public class ModuleLoaderTest
{
    @Test
    public void loadModules()
    {
        List<Module> modules;
        try
        {
            modules = ModuleLoader.loadModules(new FileInputStream(new File("src/test/resources/testModule.yaml")), str -> null, str -> null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            assertTrue(false);
            return;
        }
        assertEquals(2, modules.size());

        Module zero = modules.get(0);
        assertEquals("zero", zero.getName());
        assertEquals(2, zero.getTags().size());
        assertEquals("test", zero.getTags().get(0));
        assertEquals("module", zero.getTags().get(1));
        assertEquals(0, zero.getHiddenTags().size());

        Module one = modules.get(1);
        assertEquals("one", one.getName());
        assertEquals(2, one.getTags().size());
        assertEquals("second", one.getTags().get(0));
        assertEquals("test", one.getTags().get(1));
        assertEquals(1, one.getHiddenTags().size());
        assertEquals("module", one.getHiddenTags().get(0));
    }
}
