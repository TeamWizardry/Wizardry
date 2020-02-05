package com.teamwizardry.wizardry.api.spell;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

/**
 * Registry for mapping {@code modid:name} to Patterns for Module loading and
 * construction.
 */
public class PatternRegistry
{
    private static final HashMap<String, HashMap<String, Pattern>> patterns = new HashMap<>();

    @SafeVarargs
    public static void addPatterns(String modid,
            Pair<String, Pattern>... patterns)
    {
        HashMap<String, Pattern> modidMap = PatternRegistry.patterns
                .computeIfAbsent(modid, k -> new HashMap<String, Pattern>());
        Arrays.stream(patterns)
                .forEach(pair -> modidMap.put(pair.getLeft(), pair.getRight()));
    }

    public static Pattern getPattern(String conjoinedName)
    {
        String[] split = conjoinedName.split(":");
        if (split.length != 2) throw new IllegalArgumentException(
                "Input name " + conjoinedName + " contains "
                        + (split.length - 1) + " :, must only contain one!");
        return getPattern(split[0], split[1]);
    }

    public static Pattern getPattern(String modid, String patternName)
    {
        return patterns
                .computeIfAbsent(modid, k -> new HashMap<String, Pattern>())
                .get(patternName);
    }

    public static String getName(Pattern pattern)
    {
        return "Pattern=" + patterns.entrySet().stream()
                .filter(mapEntry -> mapEntry.getValue().containsValue(pattern))
                .findFirst().map(map -> {
                    return map.getKey() + ":" + map.getValue().entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(pattern))
                            .findFirst().get().getKey();
                }).orElse("null:null");
    }

    public static void registerPatterns()
    {
        addPatterns(Wizardry.MODID, Pair.of("burn", new Pattern() {
            @Override public void run() {}
            @Override public void affectEntity(Entity entity) {}
            @Override public void affectBlock(BlockPos pos) {}
        }));
    }
}
