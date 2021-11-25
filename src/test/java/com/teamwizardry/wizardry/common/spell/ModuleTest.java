// package com.teamwizardry.wizardry.common.spell;

// import com.teamwizardry.wizardry.common.spell.component.Module;
// import com.teamwizardry.wizardry.common.spell.component.ModuleEffect;
// import com.teamwizardry.wizardry.common.spell.component.ModuleShape;

// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.api.TestInstance.Lifecycle;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNull;

// @TestInstance(Lifecycle.PER_CLASS)
// public class ModuleTest {
//     private Module module;
//     private ModuleShape shape;
//     private ModuleEffect effect;

//     private final String name = "test";
//     private final String element = "null";
//     private final String form = "shape";
//     private final String action = "effect";

//     @BeforeAll
//     public void initTest() {
//         module = new Module(null, name, null, 10, 10, element, null, null);
//         shape = new ModuleShape(null, name, null, 10, 10, form, element, null, null);
//         effect = new ModuleEffect(null, name, null, 10, 10, action, element, null, null);
//     }

//     @Test
//     public void testname
//     {
//         assertEquals(name, module.name);
//         assertEquals(name, shape.name);
//         assertEquals(name, effect.name);
//     }
    
//     @Test
//     public void testGetPattern()
//     {
//         assertNull(module.getPattern());
//         assertNull(shape.getPattern());
//         assertNull(effect.getPattern());
//     }
    
//     @Test
//     public void testGetItem()
//     {
//         assertNull(module.items);
//         assertNull(shape.items);
//         assertNull(effect.getPattern());
//     }
    
//     @Test
//     public void testGetElement()
//     {
//         assertEquals(element, module.getElement());
//         assertEquals(element, shape.getElement());
//         assertEquals(element, effect.getElement());
//     }
    
//     @Test
//     public void testGetForm()
//     {
//         assertEquals(form, shape.getForm());
//     }
    
//     @Test
//     public void testGetAction()
//     {
//         assertEquals(action, effect.getAction());
//     }
// }
