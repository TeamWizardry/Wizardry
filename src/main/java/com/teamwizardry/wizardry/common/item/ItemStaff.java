package com.teamwizardry.wizardry.common.item;

import org.jetbrains.annotations.NotNull;

import com.teamwizardry.wizardry.common.spell.SpellCompiler;
import com.teamwizardry.wizardry.common.spell.component.Interactor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemStaff extends Item implements INacreProduct.INacreDecayProduct {
    public ItemStaff(Settings settings) {
        super(settings);
        // TODO - find where property overrides are
//        this.addPropertyOverride(new Identifier("staff_state"),
//                (stack, world, entity) -> stack.hasTag() && stack.getTag().contains(StringConsts.SPELL_DATA) ? 1 : 0);
    }

    @Override
    public @NotNull TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);

        // TODO: Test spell, delete when spell crafting is finished
        if (!world.isClient) {
            Interactor caster = new Interactor(player);
            SpellCompiler.get()
                    .compileSpell(new ItemStack(Items.BEEF),
                            new ItemStack(Items.LEATHER),
                            new ItemStack(Items.LAPIS_LAZULI),
                            new ItemStack(Items.LAPIS_LAZULI),
                            new ItemStack(Items.MAGMA_CREAM))
                    .toInstance(caster)
                    .run(world, caster);
        }

        return TypedActionResult.success(itemstack);
    }
}
