package me.lordsaad.wizardry.items;

import me.lordsaad.wizardry.Wizardry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Saad on 6/21/2016.
 */
public class ItemManaPearl extends Item {

    public ItemManaPearl() {
        setRegistryName("mana_pearl");
        setUnlocalizedName("mana_pearl");
        GameRegistry.register(this);
        setMaxStackSize(1);
        setCreativeTab(Wizardry.tab);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
