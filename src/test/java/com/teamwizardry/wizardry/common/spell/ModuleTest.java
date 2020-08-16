package com.teamwizardry.wizardry.common.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
public class ModuleTest
{
    private Module module;
    private ModuleShape shape;
    private ModuleEffect effect;
    
    private String name = "test";
    private String element = "null";
    private String form = "shape";
    private String action = "effect";
    
    private Color primary = new Color(0x123456);
    private Color secondary = new Color(0xABCDEF);
    
    @BeforeAll
    public void initTest()
    {
        module = new Module(null, name, null, element);
        shape = new ModuleShape(null, name, null, form, element);
        effect = new ModuleEffect(null, name, null, primary, secondary, action, element);
    }
    
    @Test
    public void testGetName()
    {
        assertEquals(name, module.getName());
        assertEquals(name, shape.getName());
        assertEquals(name, effect.getName());
    }
    
    @Test
    public void testGetPattern()
    {
        assertNull(module.getPattern());
        assertNull(shape.getPattern());
        assertNull(effect.getPattern());
    }
    
    @Test
    public void testGetItem()
    {
        assertNull(module.getItems());
        assertNull(shape.getItems());
        assertNull(effect.getPattern());
    }
    
    @Test
    public void testGetElement()
    {
        assertEquals(element, module.getElement());
        assertEquals(element, shape.getElement());
        assertEquals(element, effect.getElement());
    }
    
    @Test
    public void testGetForm()
    {
        assertEquals(form, shape.getForm());
    }
    
    @Test
    public void testGetAction()
    {
        assertEquals(action, effect.getAction());
    }
    
    @Test
    public void testEffectGetColors()
    {
        assertEquals(primary, effect.getPrimaryColor());
        assertEquals(secondary, effect.getSecondaryColor());
    }
}
