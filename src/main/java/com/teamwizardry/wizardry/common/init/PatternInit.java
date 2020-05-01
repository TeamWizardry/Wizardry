package com.teamwizardry.wizardry.common.init;

import java.util.function.Function;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.spell.Pattern;
import com.teamwizardry.wizardry.api.spell.PatternShape;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.IForgeRegistry;

public class PatternInit
{
    public static void init(IForgeRegistry<Pattern> registry)
    {
        // Shapes
        registry.registerAll(new TempPatternShape().setRegistryName(Wizardry.MODID, "beam"),
                             new TempPatternShape().setRegistryName(Wizardry.MODID, "cone"),
                             new TempPatternShape().setRegistryName(Wizardry.MODID, "projectile"),
                             new TempPatternShape().setRegistryName(Wizardry.MODID, "self"),
                             new TempPatternShape().setRegistryName(Wizardry.MODID, "touch"),
                             new TempPatternShape().setRegistryName(Wizardry.MODID, "zone"));
    }
    
    /**
     * Temporary class
     * <p>
     * TODO: Delete once real patterns exist
     */
    private static class TempPatternShape extends PatternShape
    {
        @Override public void run(Function<BlockPos, Boolean> shouldAffectBlock, Function<Entity, Boolean> shouldAffectEntity) {}
        @Override public void affectEntity(Entity entity) {}
        @Override public void affectBlock(BlockPos pos){}
    }
}
