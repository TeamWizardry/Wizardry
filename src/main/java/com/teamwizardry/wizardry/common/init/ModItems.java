package com.teamwizardry.wizardry.common.init;

import com.teamwizardry.wizardry.Wizardry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ModItems
{
    public static final ItemGroup wizardry = FabricItemGroupBuilder.build(Wizardry.getId("general"), () -> new ItemStack(ModItems.staff));

    public static final Item wisdomStick = new Item(new FabricItemSettings().group(wizardry));
    
    public static final Item staff = new Item(new FabricItemSettings().group(wizardry).maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item pearl = new Item(new FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON));

    public static final Item devilDust = new Item(new FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON));
    public static final Item skyDust = new Item(new FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON));
    public static final Item fairyDust = new Item(new FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON));
    public static final Item fairyWings = new Item(new FabricItemSettings().group(wizardry).rarity(Rarity.UNCOMMON));

    public static final Item fairyApple = new Item(new FabricItemSettings().group(wizardry).rarity(Rarity.RARE).food(
            new FoodComponent.Builder()
                             .hunger(5)
                             .saturationModifier(2)
                             .alwaysEdible()
                             .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20*15), 1)
                             .statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 20*60*2, 1), 1)
                             .statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20*30), 1)
                             .build()
    ));
    
    public static Item manaBucket;
    public static Item nacreBucket;

    public static void init()
    {
        initItem(wisdomStick, "wisdom_stick");
        initItem(staff, "staff");
        initItem(pearl, "pearl");
        initItem(devilDust, "devil_dust");
        initItem(skyDust, "sky_dust");
        initItem(fairyDust, "fairy_dust");
        initItem(fairyWings, "fairy_wings");
        initItem(fairyApple, "fairy_apple");
        initItem(manaBucket, "mana_bucket");
    }

    private static void initItem(Item item, String path) { Registry.register(Registry.ITEM, Wizardry.getId(path), item); }
}