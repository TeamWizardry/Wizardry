package com.teamwizardry.wizardry.common.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@TestInstance(Lifecycle.PER_CLASS)
public class ModuleTest
{
    private Module module;
    private ModuleShape shape;
    private ModuleEffect effect;
    
    private String name = "test";
    private Map<String, Range<Integer>> attributes = new HashMap<>();
    private List<String> tags = new LinkedList<>();
    private List<String> hiddenTags = new LinkedList<>();
    
    private Color primary = new Color(0x123456);
    private Color secondary = new Color(0xABCDEF);
    
    @BeforeAll
    public void initTest()
    {
        attributes.put("mana", Range.between(50, Integer.MAX_VALUE));
        attributes.put("burnoout", Range.between(25, Integer.MAX_VALUE));
        
        tags.add("test");
        hiddenTags.add("temp");
        
        module = new Module(null, name, null, attributes, tags, hiddenTags);
        shape = new ModuleShape(null, name, null, attributes, tags, hiddenTags);
        effect = new ModuleEffect(null, name, null, primary, secondary, attributes, tags, hiddenTags);
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
        assertNull(module.getItem());
        assertNull(shape.getItem());
        assertNull(effect.getPattern());
    }
    
    @ParameterizedTest
    @ValueSource(strings = { "mana", "burnout" })
    public void testGetAttributeRanges(String attribute)
    {
        final Range<Integer> range = attributes.get(attribute);
        
        assertNotNull(module.getAttributeRanges());
        assertEquals(range, module.getAttributeRanges().get(attribute));
        
        assertNotNull(shape.getAttributeRanges());
        assertEquals(range, shape.getAttributeRanges().get(attribute));
        
        assertNotNull(effect.getAttributeRanges());
        assertEquals(range, effect.getAttributeRanges().get(attribute));
    }
    
    @Test
    public void testGetTags()
    {
        assertEquals(tags, module.getTags());
        assertEquals(tags, shape.getTags());
        assertEquals(tags, effect.getTags());
    }
    
    @Test
    public void testGetHiddenTags()
    {
        assertEquals(hiddenTags, module.getHiddenTags());
        assertEquals(hiddenTags, shape.getHiddenTags());
        assertEquals(hiddenTags, effect.getHiddenTags());
    }
    
    @Test
    public void testEffectGetColors()
    {
        assertEquals(primary, effect.getPrimaryColor());
        assertEquals(secondary, effect.getSecondaryColor());
    }
}
