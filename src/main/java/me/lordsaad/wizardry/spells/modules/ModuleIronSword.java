package me.lordsaad.wizardry.spells.modules;

import me.lordsaad.wizardry.api.modules.IEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Saad on 6/21/2016.
 */
public class ModuleIronSword implements IEvent {

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.IRON_SWORD);
    }

    @Override
    public ModuleType getType() {
        return ModuleType.EFFECT;
    }

    @Override
    public void tick(World world, EntityPlayer source) {

    }
}
