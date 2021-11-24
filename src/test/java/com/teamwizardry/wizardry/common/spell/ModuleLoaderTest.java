// package com.teamwizardry.wizardry.common.spell;

// import com.teamwizardry.wizardry.Wizardry;
// import com.teamwizardry.wizardry.api.spell.*;
// import com.teamwizardry.wizardry.common.spell.component.Module;
// import com.teamwizardry.wizardry.common.spell.component.ModuleEffect;
// import com.teamwizardry.wizardry.common.spell.component.ModuleShape;
// import com.teamwizardry.wizardry.common.spell.loading.ModuleLoader;
// import net.minecraft.util.Identifier;
// import net.minecraft.world.World;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.api.TestInstance.Lifecycle;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.HashMap;
// import java.util.List;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// @TestInstance(Lifecycle.PER_CLASS)
// public class ModuleLoaderTest {
//     private final HashMap<Identifier, Pattern> testPatterns = new HashMap<>();
//     private List<Module> modules;

//     private final Identifier shapeLoc = new Identifier(Wizardry.MODID, "test_shape");
//     private final Identifier effectLoc = new Identifier(Wizardry.MODID, "test_effect");

//     @BeforeAll
//     public void initTest() {
//         testPatterns.put(shapeLoc, new PatternShape() {
//             @Override
//             public void run(World world, Instance instance, Interactor target) {
//             }

//             @Override
//             public void affectEntity(World world, Interactor entity, Instance instance) {
//             }

//             @Override
//             public void affectBlock(World world, Interactor entity, Instance instance) {
//             }
//         });
        
//         testPatterns.put(effectLoc, new PatternEffect() {
//             @Override public void run(World world, Instance instance, Interactor target) {}
//             @Override public void affectEntity(World world, Interactor entity, Instance instance) {}
//             @Override public void affectBlock(World world, Interactor entity, Instance instance) {}
//         });
        
//         try
//         {
//             modules = ModuleLoader.loadModules(new FileInputStream(new File("src/test/resources/testModule.yaml")), testPatterns::get, str -> null);
//         }
//         catch (IOException e)
//         {
//             e.printStackTrace();
//         }
//     }
    
//     @Test
//     public void testModuleCount()
//     {
//         assertEquals(2, modules.size());
//     }
    
//     @Test
//     public void testModuleShape()
//     {
//         assertTrue(modules.get(0) instanceof ModuleShape);
//         ModuleShape shape = (ModuleShape) modules.get(0);
//         assertEquals(testPatterns.get(shapeLoc), shape.getPattern());
//         assertEquals("zero", shape.getName());
//         assertEquals(0, shape.getItems().size());
//         assertEquals("test", shape.getForm());
//         assertEquals("module", shape.getElement());
//     }
    
//     @Test
//     public void testModuleEffect()
//     {
//         assertTrue(modules.get(1) instanceof ModuleEffect);
//         ModuleEffect effect = (ModuleEffect) modules.get(1);
//         assertEquals(testPatterns.get(effectLoc), effect.getPattern());
//         assertEquals("one", effect.getName());
//         assertEquals(0, effect.getItems().size());
//         assertEquals("test", effect.getAction());
//         assertEquals("module", effect.getElement());    
//     }
// }
